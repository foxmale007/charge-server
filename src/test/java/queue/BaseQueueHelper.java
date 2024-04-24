package queue;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;

/**
 * lmax.disruptor 高效队列处理模板.
 * 支持初始队列，即在init()前进行发布。
 * 
 * 调用init()时才真正启动线程开始处理
 * 系统退出自动清理资源.
 * 
 * @author JIM
 *
 * @param <D> 消息类
 * @param <E> 消息包装类
 * @param <H> 消息处理类
 */
public abstract class BaseQueueHelper<D, E extends ValueWrapper<D>, H extends WorkHandler<E>> {

	private Disruptor<E> disruptor;

	private RingBuffer<E> ringBuffer;

    private List<D> initQueue = new ArrayList<D>();

    /**
     * 需要多少线程来消耗队列.
     * @return
     */
    protected abstract int getThreadNum();

    /**
     * @return 队列长度，必须是2的幂
     */
    protected abstract int getQueueSize();

    /**
     * @return
     */
    private Class<E> eventClass;
    
    private Class<H> eventHandlerClass;
    
	//记录所有的队列，系统退出时统一清理资源
    private static List<BaseQueueHelper> queueHelperList = new ArrayList<BaseQueueHelper>();

    public BaseQueueHelper() {
        Class<?> typeCls = getClass();
        Type genType = typeCls.getGenericSuperclass();
        while (true) {
            if (!(genType instanceof ParameterizedType)) {
                typeCls = typeCls.getSuperclass();
                genType = typeCls.getGenericSuperclass();
            } else {
                break;
            }
        }
        this.eventClass = (Class<E>) ((ParameterizedType) genType).getActualTypeArguments()[1];
        this.eventHandlerClass = (Class<H>) ((ParameterizedType) genType).getActualTypeArguments()[2];
    }

	@SuppressWarnings("unchecked")
	public void init() {
		disruptor = new Disruptor<E>(new EventFactory<E>(){
			@Override
			public E newInstance() {
				try {
					return (E)eventClass.newInstance();
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}
		}, getQueueSize(), DaemonThreadFactory.INSTANCE, ProducerType.SINGLE, getStrategy());
		
		H[] eventHandlers = (H[])Array.newInstance(eventHandlerClass,getThreadNum());
		for(int i = 0 ;i < getThreadNum(); i ++) {
			try {
				eventHandlers[i] = (H)eventHandlerClass.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		disruptor.handleEventsWithWorkerPool(eventHandlers);
		ringBuffer = disruptor.start();
		
		for(D data: initQueue) {
			ringBuffer.publishEvent(new EventTranslatorOneArg<E, D>(){ //启动后阻塞式发布所有的队列消息
				@Override
				public void translateTo(E event, long sequence, D data) {
					event.setValue(data);
				}
			}, data);
		}

		//加入资源清理钩子
		synchronized(queueHelperList) {
			if(queueHelperList.isEmpty()) {
				Runtime.getRuntime().addShutdownHook(new Thread() {
					@Override
					public void run() {
						for(BaseQueueHelper baseQueueHelper: queueHelperList) {
							baseQueueHelper.shutdown();
						}
					}
				});
			}
			queueHelperList.add(this);
		}
	}

	/**
	 * 如果要改变线程执行优先级，override此策略.
	 * YieldingWaitStrategy会提高响应并在闲时占用70%以上CPU，慎用
	 * SleepingWaitStrategy会降低响应更减少CPU占用，用于日志等场景.
	 * @return
	 */
	protected WaitStrategy getStrategy() {
		return new BlockingWaitStrategy();
	}
	
	/**
	 * 插入队列消息，支持在对象init前插入队列，则在队列建立时立即发布到队列处理.
	 * 如果队列已满，则会返回false. 成功发布则返回true.
	 * @param data
	 */
	public boolean tryPublishEvent(D data) {
		synchronized(this) {
			if(ringBuffer == null) {
				initQueue.add(data);
				return true;
			}
			return ringBuffer.tryPublishEvent(new EventTranslatorOneArg<E, D>(){
				@Override
				public void translateTo(E event, long sequence, D data) {
					event.setValue(data);
				}
			}, data);
		}
	}
	
	
	/**
	 * 插入队列消息，支持在对象init前插入队列，则在队列建立时立即发布到队列处理.
	 * 如果队列已满，则会阻塞线程直到可用.
	 * @param data
	 */
	public void publishEvent(D data) {
		synchronized(this) {
			if(ringBuffer == null) {
				initQueue.add(data);
				return;
			}
			ringBuffer.publishEvent(new EventTranslatorOneArg<E, D>(){
				@Override
				public void translateTo(E event, long sequence, D data) {
					event.setValue(data);
				}
			}, data);
		}
	}
	
	public void shutdown() {
		disruptor.shutdown();
	}
}

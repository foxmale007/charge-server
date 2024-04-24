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
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;

import eu.chargetime.ocpp.test.JSONTestClient;

//@Component
public class OcppActionQueueHelper{

	private Disruptor<QcppActionEvent> disruptor;

	private RingBuffer<QcppActionEvent> ringBuffer;

    private List<OcppAction> initQueue = new ArrayList<OcppAction>();

    /**
     * 需要多少线程来消耗队列.
     * @return
     */
    protected int getThreadNum() {
    	return 1;
    }

    /**
     * @return 队列长度，必须是2的幂
     */
    protected int getQueueSize() {
    	return 8;
    }

    private JSONTestClient client;
    
	//记录所有的队列，系统退出时统一清理资源
    private static List<OcppActionQueueHelper> queueHelperList = new ArrayList<OcppActionQueueHelper>();

    public OcppActionQueueHelper() {}
    
    public OcppActionQueueHelper(JSONTestClient client) {
    	this.client = client;
    }

	public void setClient(JSONTestClient client) {
		this.client = client;
	}

	@SuppressWarnings("unchecked")
	public void init() {
		disruptor = new Disruptor<QcppActionEvent>(new EventFactory<QcppActionEvent>(){
			@Override
			public QcppActionEvent newInstance() {
				return new QcppActionEvent();
			}
		}, getQueueSize(), DaemonThreadFactory.INSTANCE, ProducerType.SINGLE, getStrategy());
		
		QcppActionEventHandler[] eventHandlers = new QcppActionEventHandler[getThreadNum()];
		for(int i = 0 ;i < getThreadNum(); i ++) {
			try {
				eventHandlers[i] = new QcppActionEventHandler(client);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		disruptor.handleEventsWithWorkerPool(eventHandlers);
		ringBuffer = disruptor.start();
		
		for(OcppAction data: initQueue) {
			ringBuffer.publishEvent(new EventTranslatorOneArg<QcppActionEvent, OcppAction>(){ //启动后阻塞式发布所有的队列消息
				@Override
				public void translateTo(QcppActionEvent event, long sequence, OcppAction data) {
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
						for(OcppActionQueueHelper baseQueueHelper: queueHelperList) {
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
	public boolean tryPublishEvent(OcppAction data) {
		synchronized(this) {
			if(ringBuffer == null) {
				initQueue.add(data);
				return true;
			}
			return ringBuffer.tryPublishEvent(new EventTranslatorOneArg<QcppActionEvent, OcppAction>(){
				@Override
				public void translateTo(QcppActionEvent event, long sequence, OcppAction data) {
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
	public void publishEvent(OcppAction data) {
		synchronized(this) {
			if(ringBuffer == null) {
				initQueue.add(data);
				return;
			}
			ringBuffer.publishEvent(new EventTranslatorOneArg<QcppActionEvent, OcppAction>(){
				@Override
				public void translateTo(QcppActionEvent event, long sequence, OcppAction data) {
					event.setValue(data);
				}
			}, data);
		}
	}
	
	public void shutdown() {
		disruptor.shutdown();
	}
//	@Override
//	public void afterPropertiesSet() throws Exception {
//		this.init();
//	}

}

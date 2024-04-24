package org.ccframe.config;

/**
 * 项目常量定义，继承于系统常量，可以在里面对系统常量进行覆盖及扩展.
 * 用于继承子项目
 * @author Jim
 *
 */
public interface GlobalEx extends Global{
	
	int OCPP_HEARBEAT_INTERVAL = 60; //心跳间隔秒
	
	String OCPP_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";

	String CACHKEY_HEARTBEAT_PERFIX = "ocppHeartbeat_";

	String CACHEREGION_HEARTBEAT = "heartbeat";

//	public static enum TreeRootEnum{
//
//		MENU_TREE_ROOT(2),
//		ARTICLE_CATEGORY_TREE_ROOT(3),
//		MY_ROOT(7);
//
//		private int treeNodeId;
//		private TreeRootEnum(int treeNodeId){
//			this.treeNodeId = treeNodeId;
//		}
//		public int getTreeNodeId(){
//			return treeNodeId;
//		}
//	}
}

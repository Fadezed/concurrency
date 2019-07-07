package com.example.concurrency.patterns.lockordering;


/**
 * 描述:
 * 处理锁顺序问题 （防止死锁）@link com.example.concurrency.features.synchronizedcase.SynchronizedResolveDeadLock.SortExeAccount
 * 适用场景：处理多个锁时
 * @author zed
 * @since 2019-06-30 10:59 AM
 */
public class FixedLockOrdering {

	private static class LockableObject {
		/**
		 * id
		 */
		private int id;
		/**
		 * another
		 */
		private String anotherValue;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getAnotherValue() {
			return anotherValue;
		}

		public void setAnotherValue(String anotherValue) {
			this.anotherValue = anotherValue;
		}
	}

	/**
	 * 多个对象锁操作 例如银行转账
	 * @param obj1 o1
	 * @param obj2 o2
	 */
	public void doSomeOperation(LockableObject obj1, LockableObject obj2) {
		LockableObject left = obj1;
		LockableObject right = obj2;
		if (obj1.getId() > obj2.getId()) {
			left = obj2;
			right = obj1;
		}
		// 锁定序号小的账户
		synchronized(left){
			// 锁定序号大的账户
			synchronized(right){
				//do something
			}
		}
	}

}

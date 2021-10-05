package nachos.threads;

import nachos.machine.*;

/**
 * An implementation of condition variables that disables interrupt()s for
 * synchronization.
 *
 * <p>
 * You must implement this.
 *
 * @see	nachos.threads.Condition
 */
public class Condition2 {
    /**
     * Allocate a new condition variable.
     *
     * @param	conditionLock	the lock associated with this condition
     *				variable. The current thread must hold this
     *				lock whenever it uses <tt>sleep()</tt>,
     *				<tt>wake()</tt>, or <tt>wakeAll()</tt>.
     */
    public Condition2(Lock conditionLock) {
	this.conditionLock = conditionLock;
    }

    /**
     * Atomically release the associated lock and go to sleep on this condition
     * variable until another thread wakes it using <tt>wake()</tt>. The
     * current thread must hold the associated lock. The thread will
     * automatically reacquire the lock before <tt>sleep()</tt> returns.
     */
    
    //wait()
    @SuppressWarnings("static-access")
	public void sleep() {
	Lib.assertTrue(conditionLock.isHeldByCurrentThread());
	
	//Enqueue thread to waitQ
	waitQ.waitForAccess(KThread.currentThread());
	
	//Release the lock
	conditionLock.release();
	
	//Suspend the thread
	KThread.currentThread().sleep();
	
	//Acquire the lock
	conditionLock.acquire();
    }

    /**
     * Wake up at most one thread sleeping on this condition variable. The
     * current thread must hold the associated lock.
     */
    
    //signal()
    public void wake() {
	Lib.assertTrue(conditionLock.isHeldByCurrentThread());
	
	//If waitQ is not empty
	if(waitQ.nextThread() != null)
	{
		//Dequeue the next process from waitQ
		KThread temp = waitQ.nextThread();
		
		//Place next process in the scheduler's ready queue
		temp.ready();
		
	}
	
	
    }

    /**
     * Wake up all threads sleeping on this condition variable. The current
     * thread must hold the associated lock.
     */
    
    //signalAll()
    public void wakeAll() {
	Lib.assertTrue(conditionLock.isHeldByCurrentThread());
	
	while(waitQ.nextThread() != null)
	{
		//Dequeue the next process from waitQ
		KThread temp = waitQ.nextThread();
		
		//Place next process in the scheduler's ready queue
		temp.ready();
	}
	
    }

    private Lock conditionLock;
    
    private ThreadQueue waitQ = ThreadedKernel.scheduler.newThreadQueue(true);
}
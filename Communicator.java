package nachos.threads;

import nachos.machine.*;

/**
 * A <i>communicator</i> allows threads to synchronously exchange 32-bit
 * messages. Multiple threads can be waiting to <i>speak</i>,
 * and multiple threads can be waiting to <i>listen</i>. But there should never
 * be a time when both a speaker and a listener are waiting, because the two
 * threads can be paired off at this point.
 */
public class Communicator {
    /**
     * Allocate a new communicator.
     */
    public Communicator() {
    	
    }

    /**
     * Wait for a thread to listen through this communicator, and then transfer
     * <i>word</i> to the listener.
     *
     * <p>
     * Does not return until this thread is paired up with a listening thread.
     * Exactly one listener should receive <i>word</i>.
     *
     * @param	word	the integer to transfer.
     */
    public void speak(int word) {
    	l.acquire();
    	
    	while(hasSpeaker)
    	{
    		speakerWaitQueue.sleep();
    	}
    	
    	hasSpeaker = true;
    	
    	Message.setMessage(word);
    	
    	while(!hasListener || !hasGotMsg)
    	{
    		lCV.wake();
    		sCV.sleep();
    	}
    	
    	hasListener = false;
    	hasSpeaker = false;
    	hasGotMsg = false;
    	
    	speakerWaitQueue.wake();
    	listenerWaitQueue.wake();

    	l.release();    	
    }

    /**
     * Wait for a thread to speak through this communicator, and then return
     * the <i>word</i> that thread passed to <tt>speak()</tt>.
     *
     * @return	the integer transferred.
     */    
    public int listen() {
    	l.acquire();

    	while(hasListener)
    	{
    		listenerWaitQueue.sleep();
    	}
    	
    	hasListener = true;
    	
    	while(!hasSpeaker)
    	{
    		lCV.sleep();
    	}
    	
    	sCV.wake();
    	Lib.assertTrue(Message.HasMessage());
    	hasGotMsg = true;
    	
    	l.release();
	return Message.getMessage();
    }
        
    private static class Message
    {
    	static int _message;
    	static boolean _hasMessage = false;
    	public static void setMessage(int message)
    	{
    		_message = message;
    		_hasMessage = true;
    	}
    	
    	public static int getMessage()
    	{
    		_hasMessage = false;
    		return _message;
    	}
    	
    	public static boolean HasMessage()
    	{
    		return _hasMessage;
    	}
    }
    
    private Lock l = new Lock();
    private Condition2 sCV = new Condition2(l);
    private Condition2 lCV = new Condition2(l);
    private Condition2 speakerWaitQueue = new Condition2(l);
    private Condition2 listenerWaitQueue = new Condition2(l);
    
    private boolean hasSpeaker = false;
    private boolean hasListener = false;
    private boolean hasGotMsg = false;
}

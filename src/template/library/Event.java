package template.library;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

import processing.core.*;

/**
 * This is a simple but awesome library! It provides events for your processing sketches. 
 * These events could be created in your classes or simply globally. 
 * A couple of examples: onLoaded, onOverlap, onHit, onMove, onOpen, onDestroy, onThreadTerminate…
 * 
 * Usage
 *  - create an Event in the class that triggers the event. Although, events could also be triggered somewhere else.
 *  - pass in the Event(...) constructor the parameter classes that will be passed to the listener functions
 *  - call Event::trigger(...) and pass in the parameters of the event. Must match the types passed in the constructor
 *  - to add listeners create a function with the matching parameters of the specific Event. Then call bind(Object listener, String functionName)
 * 
 * Advantages of Event library
 *  - You do not need to implement an interface for each classes that need to listen to the event
 *  - You do not need to create an interface per event
 *  - You do not need to create all you function listener “templates” with the keyword default in a centralized interface
 *  - This library has nothing to do with interface
 *  - You can listen to an event and host an event in the main class
 * 
 * @example World
 * */

public class Event {
	
	public final static String VERSION = "##library.prettyVersion##";
	
    private Class<?>[] args;
    private ArrayList<EventListener> eventListeners;
    
    private class EventListener {
        public Method method;
        public Object listener;
        
        public EventListener(Object listener, Method method) {
            this.listener = listener;
            this.method = method;
        }
        
        public void invoke(Object[] arguments) {
            try {
                method.invoke(listener, arguments);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
        
        @Override
        public boolean equals(Object obj) {
            if(obj instanceof EventListener) {
                EventListener other = (EventListener)obj;
                return other.listener.equals(listener) && other.method.equals(method);
            }
            
            return false;
        }
        
        @Override
        public int hashCode() {
            int className = method.getDeclaringClass().getName().hashCode();
            int functionName = method.getName().hashCode();
            int parameters = method.getParameterTypes().hashCode();
            
            return className ^ functionName ^ parameters;
        }
    }
    
    /**
     * Defines what are the parameter types of the event
     * 
     * @param args
     */
    public Event(Class<?>... args) {
        this.args = args;
        this.eventListeners = new ArrayList<EventListener>();
    }
    
    /**
     * The trigger function is used to trigger the function. This will call all the events listeners bound to the event.
     * 
     * @param arguments the arguments passed to the event listeners. Must match the types passed in the Event constructor
     */
    public void trigger(Object... arguments) {
        for(EventListener eventListener : eventListeners) {
            try {
                eventListener.invoke(arguments);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Will bind a function listener to the event.
     * The function's parameter types must match the ones provided in the Event constructor
     * 
     * @param listener a reference to the class holding the function
     * @param name the name of the function
     */
    public void bind(Object listener, String name) {
        Class<?> c = listener.getClass();
        
        try {
            Method method = c.getMethod(name, args);
            EventListener eventListener = new EventListener(listener, method);
            
            if(!eventListeners.contains(eventListener)) {
                eventListeners.add(new EventListener(listener, method));
            }
            else {
                System.err.println("<Error> could not bind event listener \""+name+"\". It is already bound!");
            }
        }
        catch(Exception e) {
            System.err.println("<Error> could not bind event listener \""+name+"\". Make sure the listener and the function is valid. The parameter types must match the ones provided in the Event constructor");
        }
    }
    
    /**
     * Will unbind a function listener to the event.
     * 
     * @param listener a reference to the class holding the function
     * @param name the name of the function
     */
    public void unbind(Object listener, String name) {
         Class<?> c = listener.getClass();
         
         try {
             Method method = c.getMethod(name, args);
             EventListener eventListener = new EventListener(listener, method);
             
             if(eventListeners.contains(eventListener)) {
                 eventListeners.remove(eventListener);
             }
             else {
                 System.err.println("<Error> could not unbind event listener \""+name+"\". Make sure the listener is already bound");
             }
         }
         catch(Exception e) {
            System.err.println("<Error> could not unbind event listener \""+name+"\". Make sure the listener and the function is valid. The parameter types must match the ones provided in the Event constructor");
        }
    }
    
    /**
     * The bound function returns whether the function is bound to this event or not
     * 
     * @param listener a reference to the class holding the function
     * @param name the name of the function
     * 
     * @return boolean is it bound?
     */
    public boolean bound(Object listener, String name) {
        Class<?> c = listener.getClass();
         
         try {
             Method method = c.getMethod(name, args);
             EventListener eventListener = new EventListener(listener, method);
             
             return eventListeners.contains(eventListener);
         }
         catch(Exception e) {
            System.err.println("<Error> Event : invalid function \""+name+"\" provided in bound()");
        }
        
        return false;
    }
}
    
  
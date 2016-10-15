package com.github.xzzpig.pigapi.event;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Event {
	private static final HashMap<Type, List<EventMethod>> events = new HashMap<Type, List<EventMethod>>();

	public static final void registListener(Listener listener) {
		for (Method meth : listener.getClass().getMethods()) {
			for (Annotation ann : meth.getAnnotations()) {
				if (ann.toString().contains("EventHandler")) {
					meth.setAccessible(true);
					try {
						Type type = meth.getGenericParameterTypes()[0];
						if (!events.containsKey(type))
							events.put(type, new ArrayList<EventMethod>());
						events.get(type).add(new EventMethod(meth, listener));
					} catch (Exception e) {
					}
				}
			}
		}
	}

	public static final void callEvent(Event event) {
		Type eventtype = event.getClass();
		if (!events.containsKey(eventtype))
			return;
		for (EventMethod em : events.get(eventtype)) {
			try {
				em.method.invoke(em.listener, new Object[] { event });
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("触发事件错误");
			}
		}
	}
}

class EventMethod {
	Method method;
	Listener listener;

	EventMethod(Method method, Listener listener) {
		this.method = method;
		this.listener = listener;
	}
}

package com.swsandbox.util;

import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * User: jgreenwald
 * Date: 7/13/13
 * Time: 1:12 PM
 */
public class SimplePool<T>
{
    private final BlockingQueue<T> objects;

    public SimplePool(Collection<T> objects)
    {
        this.objects = new ArrayBlockingQueue<T>(objects.size(), false, objects);
    }

    public T take() throws InterruptedException
    {
        return this.objects.take();
    }

    public void put(T object)
    {
        this.objects.offer(object);
    }
}

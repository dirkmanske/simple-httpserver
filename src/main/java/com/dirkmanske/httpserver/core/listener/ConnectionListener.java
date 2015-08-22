package com.dirkmanske.httpserver.core.listener;

import java.util.concurrent.Callable;

/**
 *
 * @author dirk.manske
 */
public interface ConnectionListener extends Callable<Void> {

    String getName();

}

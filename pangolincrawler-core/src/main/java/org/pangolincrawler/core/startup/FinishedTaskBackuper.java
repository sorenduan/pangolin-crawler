package org.pangolincrawler.core.startup;

/**
 * a cron job for backup fininsed task.
 */
public class FinishedTaskBackuper implements Runnable {

  private static Thread runingThread = null;


  @Override
  public void run() {
  }

  public static void start() {
    runingThread = new Thread(new FinishedTaskBackuper());
    runingThread.start();
  }
}

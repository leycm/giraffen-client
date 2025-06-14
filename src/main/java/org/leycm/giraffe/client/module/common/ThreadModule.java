package org.leycm.giraffe.client.module.common;

public abstract class ThreadModule extends BaseModule {

    private Thread moduleThread;
    private int interval;


    protected ThreadModule(String displayName, String category, String id) {
        super(displayName, category, id);
    }

    @Override
    public boolean enable() {
        if (moduleThread == null) {
            moduleThread = new Thread(() -> {
                while (running) {
                    try {
                        //noinspection BusyWait
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        disable();
                    }
                    onThreadCall();
                }
            });
            moduleThread.start();
        }
        return super.enable();
    }

    @Override
    public boolean disable() {
        if (moduleThread != null) {
            moduleThread.interrupt();
            moduleThread = null;
        }
        return super.disable();
    }

    protected abstract void onThreadCall();
}

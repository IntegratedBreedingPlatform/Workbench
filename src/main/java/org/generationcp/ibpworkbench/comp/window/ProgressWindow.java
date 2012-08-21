package org.generationcp.ibpworkbench.comp.window;

import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class ProgressWindow extends Window {
    private static final long serialVersionUID = 1L;
    private ProgressIndicator progressIndicator;
    
    private ProgressThread progressThread;
    
    public ProgressWindow(String progressCaption, long progressMillis) {
        setWidth("250px");
        setHeight("90px");
        
        progressThread = new ProgressThread(progressMillis);
        
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        
        progressIndicator = new ProgressIndicator();
        progressIndicator.setIndeterminate(false);
        progressIndicator.setCaption(progressCaption);
        
        layout.addComponent(progressIndicator);
        
        addComponent(layout);
    }
    
    public void startProgress() {
        progressThread.start();
        progressIndicator.setValue(0f);
    }
    
    private class ProgressThread extends Thread {
        private long progressMillis;
        
        public ProgressThread(long progressMillis) {
            this.progressMillis = progressMillis;
        }
        
        @Override
        public void run() {
            long progressPoints = 10;
            long progressAmount = progressMillis / progressPoints;
            
            for (int progressPoint = 0; progressPoint < 10; progressPoint++) {
                try {
                    Thread.sleep(progressAmount);
                    synchronized (getApplication()) {
                        progressIndicator.setValue(progressPoint / (float) progressPoints);
                    }
                }
                catch (InterruptedException e) {
                }
            }
            
            synchronized (getApplication()) {
                Window window = ProgressWindow.this;
                Window parentWindow = window.getParent();
                
                parentWindow.removeWindow(window);
            }
        }
    }
}

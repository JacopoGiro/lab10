package it.unibo.mvc;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * controller
 */
public final class DrawNumberApp implements DrawNumberViewObserver {
    private final DrawNumber model;
    private final List<DrawNumberView> views;

    /**
     * @param views
     *            the views to attach
     * @throws IOException 
     * @throws NumberFormatException 
     */
    public DrawNumberApp(final DrawNumberView... views) throws NumberFormatException, IOException {
        /*
         * Side-effect proof
         */
        this.views = Arrays.asList(Arrays.copyOf(views, views.length));
        for (final DrawNumberView view: views) {
            view.setObserver(this);
            view.start();
        }
        Configuration mainConfig = getConfigFromYML();
        this.model = new DrawNumberImpl(
            mainConfig.getMin(), 
            mainConfig.getMax(), 
            mainConfig.getAttempts()
        );
    }

    private Configuration getConfigFromYML() throws NumberFormatException, IOException{
        BufferedReader bStream = new BufferedReader(
            new InputStreamReader(
                getClass().getResourceAsStream("/config.yml"))
        );
        return new Configuration.Builder()
            .setMin(Integer.valueOf(bStream.readLine().split(": ")[1]))
            .setMax(Integer.valueOf(bStream.readLine().split(": ")[1]))
            .setAttempts(Integer.valueOf(bStream.readLine().split(": ")[1]))
            .build();
    }

    @Override
    public void newAttempt(final int n) {
        try {
            final DrawResult result = model.attempt(n);
            for (final DrawNumberView view: views) {
                view.result(result);
            }
        } catch (IllegalArgumentException e) {
            for (final DrawNumberView view: views) {
                view.numberIncorrect();
            }
        }
    }

    @Override
    public void resetGame() {
        this.model.reset();
    }

    @Override
    public void quit() {
        /*
         * A bit harsh. A good application should configure the graphics to exit by
         * natural termination when closing is hit. To do things more cleanly, attention
         * should be paid to alive threads, as the application would continue to persist
         * until the last thread terminates.
         */
        System.exit(0);
    }

    /**
     * @param args
     *            ignored
     * @throws IOException 
     * @throws NumberFormatException 
     */
    public static void main(final String... args) throws NumberFormatException, IOException {
        new DrawNumberApp(new DrawNumberViewImpl());
    }
}

package com.taugame.tau.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.RootPanel;
import com.taugame.tau.client.StateController.State;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Tau implements EntryPoint, Initializer {
    /**
     * The message displayed to the user when the server cannot be reached or
     * returns an error.
     */
    @SuppressWarnings("unused")
    private static final String SERVER_ERROR = "An error occurred while "
            + "attempting to contact the server. Please check your network "
            + "connection and try again.";

    /**
     * Create a remote service proxy to talk to the server-side Tau service.
     */

    GameModel gameModel;
    StateController stateController;

    public void onModuleLoad() {
        CometMessageHandler.exportSendBoardUpdate();
        CometMessageHandler.setInitializer(this);
        CometMessageHandler.exportInitialize();
        CometMessageHandler.exportRestart();

        // this delay is here to fix the "throbber of doom" in chrome and
        // firefox. Basically, we can't start the persistent GET until the
        // initial page load has completely finished and a small delay is
        // necessary to guarantee that.
        new Timer() {
            @Override
            public void run() {
                startListening();
            }
        }.schedule(500);
    }

    @Override public void reinitialize() {
        Logger.log("reinitializing");
        startListening();
        stateController.forceChangeState(State.FULL_RESTART);
    }

    @Override public void initialize() {
        NativeEventDispatcher.exportBodyKeyPressHandler();
        RootPanel.getBodyElement().setAttribute("onkeypress", "window.bodyKeyPressHandler(event);");

        stateController = new StateController(RootPanel.get("game"));
        stateController.changeState(State.NONE, State.START);
    }

    private native void startListening() /*-{
        $wnd.l();
    }-*/;
}

package ru.complitex.common.wicket.util;

import org.apache.wicket.Application;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.ws.WebSocketSettings;
import org.apache.wicket.protocol.ws.api.IWebSocketConnection;
import org.apache.wicket.protocol.ws.api.message.IWebSocketPushMessage;
import org.apache.wicket.protocol.ws.api.registry.PageIdKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ivanov Anatoliy
 */
public class WebSockets {
    private final static Logger log = LoggerFactory.getLogger(WebSockets.class);

    public static class TextMessage implements IWebSocketPushMessage {
        private final String text;

        public TextMessage(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }

    public static class CommandMessage implements IWebSocketPushMessage {
        private final String command;

        public CommandMessage(String command) {
            this.command = command;
        }

        public String getCommand() {
            return command;
        }
    }

    public static void sendMessage(IWebSocketPushMessage message, int pageId) {
        WebSocketSettings.Holder.get(Application.get()).getWebSocketPushMessageExecutor().run(() -> {
            try {
                Application application = Application.get();
                Session session = Session.get();

                if (session.getId() != null) {
                    IWebSocketConnection webSocketConnection = WebSocketSettings.Holder.get(application)
                            .getConnectionRegistry()
                            .getConnection(application, session.getId(), new PageIdKey(pageId));

                    if (webSocketConnection != null) {
                        webSocketConnection.sendMessage(message);
                    }
                }
            } catch (Exception e) {
                log.error("error sendMessage ", e);
            }
        });
    }
}

/*
 * DmitriyMX <dimon550@gmail.com>
 * 2017-05-12
 */
package lwjake2.logger.log4j2;

import lwjake2.client.Console;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.Serializable;

@Plugin(name = "QConsole", category = "Core", elementType = "appender", printObject = true)
public class QConsoleAppender extends AbstractAppender {
    @PluginFactory
    public static QConsoleAppender createAppender (
            @PluginAttribute("name") String name,
            @PluginElement("Layout") Layout<? extends Serializable> layout,
            @PluginElement("Filter") final Filter filter
    ) {
        if (name == null) {
            LOGGER.error("No name provided for QConsoleAppender");
            return null;
        }
        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }
        return new QConsoleAppender(name, filter, layout, true);
    }

    protected QConsoleAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions) {
        super(name, filter, layout, ignoreExceptions);
    }

    @Override
    public void append(LogEvent event) {
        String msg = new String(getLayout().toByteArray(event));
        Console.Print(msg+"\n");
    }
}

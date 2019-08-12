package ph.adamw.calloji.util;

import com.google.common.reflect.ClassPath;
import lombok.extern.log4j.Log4j2;
import ph.adamw.calloji.packet.PacketLinkBase;
import ph.adamw.calloji.packet.PacketLinkType;

import java.io.IOException;

@Log4j2
public class PacketLinkUtils {
    public static PacketLinkBase buildChain() throws IOException {
        PacketLinkBase linkChain = null;
        PacketLinkBase top = null;
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();

        //Reflective packet link loader
        for (final ClassPath.ClassInfo info : ClassPath.from(loader).getTopLevelClasses()) {
            if (info.getName().startsWith("ph.adamw.calloji")) {
                final Class<?> clazz = info.load();

                if(!clazz.isAnnotationPresent(PacketLinkType.class)) {
                    continue;
                }

                try {
                    final PacketLinkBase plink = (PacketLinkBase) clazz.newInstance();

                    if(linkChain == null) {
                        linkChain = top = plink;
                    } else {
                        linkChain = linkChain.setSuccessor(plink);
                    }

                    log.info("Loaded "  + clazz.getSimpleName() + " into the packet link chain.");
                } catch (InstantiationException | IllegalAccessException e) {
                    log.info("Unable to load " + clazz.getSimpleName() + " into the packet link chain.");
                    e.printStackTrace();
                }
            }
        }

        return top;
    }
}

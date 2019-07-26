package ph.adamw.calloji.util;

import com.google.common.reflect.ClassPath;
import lombok.extern.slf4j.Slf4j;
import ph.adamw.calloji.packet.PacketLinkBase;
import ph.adamw.calloji.packet.PacketLinkType;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

@Slf4j
public class PacketLinkUtils {
    public static PacketLinkBase buildChain() throws IOException {
        return buildChain(null);
    }

    public static PacketLinkBase buildChain(Object inst) throws IOException {
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
                    final PacketLinkBase plink;

                    if(inst == null) {
                         plink = (PacketLinkBase) clazz.newInstance();
                    } else {
                        plink = (PacketLinkBase) clazz.getConstructors()[0].newInstance(inst);
                    }

                    if(linkChain == null) {
                        log.debug("Chain start: " + clazz.getSimpleName());
                        linkChain = top = plink;
                    } else {
                        linkChain = linkChain.setSuccessor(plink);
                    }

                    log.info("Loaded "  + clazz.getSimpleName() + " into the packet link chain.");
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    log.info("Unable to load " + clazz.getSimpleName() + " into the packet link chain.");
                    e.printStackTrace();
                }
            }
        }

        return top;
    }
}

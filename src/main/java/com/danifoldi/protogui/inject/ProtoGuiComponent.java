package com.danifoldi.protogui.inject;

import com.danifoldi.protogui.main.ProtoGuiLoader;
import com.danifoldi.protogui.platform.PlatformInteraction;
import com.danifoldi.protogui.platform.bungee.ProtoGui;
import dagger.BindsInstance;
import dagger.Component;
import org.jetbrains.annotations.NotNull;

import javax.inject.Singleton;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

@Singleton
@Component(modules = {
        ProtoGuiBindingModule.class
})
public interface ProtoGuiComponent {

    @NotNull ProtoGuiLoader loader();

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder logger(final @NotNull Logger logger);

        @BindsInstance
        Builder datafolder(final @NotNull Path datafolder);

        @BindsInstance
        Builder threadPool(final @NotNull ExecutorService threadPool);

        @BindsInstance
        Builder platformInteraction(final @NotNull PlatformInteraction platformInteraction);

        ProtoGuiComponent build();
    }
}
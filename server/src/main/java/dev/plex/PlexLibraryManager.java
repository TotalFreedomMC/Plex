package dev.plex;

import com.google.gson.Gson;
import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jetbrains.annotations.NotNull;

public class PlexLibraryManager implements PluginLoader
{
    @Override
    public void classloader(@NotNull PluginClasspathBuilder classpathBuilder)
    {
        MavenLibraryResolver resolver = new MavenLibraryResolver();
        PluginLibraries pluginLibraries = load();
        pluginLibraries.asDependencies().forEach(resolver::addDependency);
        pluginLibraries.asRepositories().forEach(resolver::addRepository);
        // The plugin is null, a hacky way to check whether to load Jetty or not
        if (new File("plugins/Plex/modules/Module-HTTPD.jar").isFile())
        {
            resolver.addDependency(new Dependency(new DefaultArtifact("org.eclipse.jetty:jetty-server:12.0.14"), null));
            resolver.addDependency(new Dependency(new DefaultArtifact("org.eclipse.jetty.ee10:jetty-ee10-servlet:12.0.14"), null));
            resolver.addDependency(new Dependency(new DefaultArtifact("org.eclipse.jetty:jetty-proxy:12.0.14"), null));
        }
        classpathBuilder.addLibrary(resolver);
    }

    public PluginLibraries load()
    {
        try (var in = getClass().getResourceAsStream("/paper-libraries.json"))
        {
            return new Gson().fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), PluginLibraries.class);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private record PluginLibraries(Map<String, String> repositories, List<String> dependencies)
    {
        public Stream<Dependency> asDependencies()
        {
            return dependencies.stream()
                    .map(d -> new Dependency(new DefaultArtifact(d), null));
        }

        public Stream<RemoteRepository> asRepositories()
        {
            return repositories.entrySet().stream()
                    .map(e -> new RemoteRepository.Builder(e.getKey(), "default", e.getValue()).build());
        }
    }
}

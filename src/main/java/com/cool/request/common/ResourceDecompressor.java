/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * ResourceDecompressor.java is part of Cool Request
 *
 * License: GPL-3.0+
 *
 * Cool Request is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cool Request is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cool Request.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cool.request.common;

import com.intellij.openapi.components.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static com.cool.request.common.constant.CoolRequestConfigConstant.CONFIG_WORK_HOME;

@Service
public final class ResourceDecompressor {
    private static final ClassPathDataCopier classPathDataCopier = new ClassPathDataCopier();
    private static final Decompressor javaLibDecompressor = new LibraryDecompressor(List.of(
            "cool-request-script-api.jar",
            "cool-request-agent.jar",
            "spring-invoke-starter.jar"
    ), classPathDataCopier);
    private static final Decompressor scriptLibDecompressor =
            new LibraryDecompressor(List.of("cool-request-script-api.jar"), classPathDataCopier);
    private static final Decompressor javacLibDecompressor =
            new LibraryDecompressor(List.of("javac.jar"), classPathDataCopier);

    public static Decompressor getJavaLibDecompressor() {
        return javaLibDecompressor;
    }

    public static Decompressor getScriptLibDecompressor() {
        return scriptLibDecompressor;
    }

    public static Decompressor getJavacLibDecompressor() {
        return javacLibDecompressor;
    }

    interface DataCopier {
        void copyData(String source, String destination) throws IOException;
    }

    public interface Decompressor {
        public void decompressor();
    }

    public static class LibraryDecompressor implements Decompressor {
        private final List<String> libNames;
        private final DataCopier dataCopier;

        public LibraryDecompressor(List<String> libNames, DataCopier dataCopier) {
            this.libNames = libNames;
            this.dataCopier = dataCopier;
        }

        @Override
        public void decompressor() {
            for (String name : libNames) {
                String target = Paths.get(CONFIG_WORK_HOME.toString(), "lib", name).toString();
                try {
                    dataCopier.copyData("/lib/".concat(name), target);
                } catch (IOException ignored) {
                }
            }
        }
    }

    static class ClassPathDataCopier implements DataCopier {
        @Override
        public void copyData(String source, String destination) {
            URL resource = getClass().getResource(source);
            if (resource == null) {
                return;
            }
            Path path = Paths.get(destination);
            if (!path.getParent().toFile().exists()) path.getParent().toFile().mkdirs();

            try (InputStream inputStream = resource.openStream()) {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                int nRead;
                byte[] data = new byte[8092];
                while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }
                Files.write(path, buffer.toByteArray());
            } catch (IOException ignored) {
            }
        }
    }

}

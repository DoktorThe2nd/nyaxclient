package com.doktorthe2nd.min.luaj.loaders;

import com.doktorthe2nd.min.Consts;
import com.doktorthe2nd.min.MainActivity;

import org.luaj.vm2.lib.ResourceFinder;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class LuaFromImportedLoader implements ResourceFinder {
    private final String root = MainActivity.appContext.getFilesDir()+File.separator+Consts.luaImportedScripts;

    @Override
    public InputStream findResource(String filename) {
        try {
            File candidate = new File(root, filename);
            // Проверка безопасности: путь не должен выходить за пределы корня
            String canon = candidate.getCanonicalPath();
            String rootCanon = new File(root).getCanonicalPath();
            if (canon.startsWith(rootCanon + File.separator) || canon.equals(rootCanon)) {
                if (candidate.exists() && candidate.isFile()) {
                    return new FileInputStream(candidate);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public List<String> walk() {
        List<String> result = new ArrayList<>();
        try {
            File rootDir = new File(root);
            if (!rootDir.exists() || !rootDir.isDirectory()) {
                return result; // пусто, если корня нет
            }
            String rootCanon = rootDir.getCanonicalPath();
            collectFilesRecursively(rootDir, rootCanon, "", result);
        } catch (Exception e) {
            // Логирование или проброс исключения в зависимости от требований
            throw new RuntimeException("Failed to list resources", e);
        }
        return result;
    }

    private void collectFilesRecursively(File currentDir, String rootCanon, String relativePath, List<String> acc) throws Exception {
        File[] children = currentDir.listFiles();
        if (children == null) {
            return; // нет доступа или не директория
        }
        for (File child : children) {
            // Проверяем, что канонический путь дочернего элемента находится внутри корня
            String childCanon = child.getCanonicalPath();
            if (!childCanon.startsWith(rootCanon + File.separator) && !childCanon.equals(rootCanon)) {
                continue; // пропускаем символические ссылки наружу
            }
            String childRelative = relativePath.isEmpty() ? child.getName() : relativePath + "/" + child.getName();
            if (child.isFile()) {
                acc.add(childRelative); // добавляем относительный путь к файлу
            } else if (child.isDirectory()) {
                collectFilesRecursively(child, rootCanon, childRelative, acc);
            }
        }
    }
}

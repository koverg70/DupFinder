package dupfinder
import groovy.io.FileType;
import groovy.transform.CompileStatic;
import groovy.transform.ToString;

import Utils.*

import java.security.MessageDigest;

@ToString(includePackage=false)
class FileDesc
{
    String path;
    String name;
    Date modified;
    long length;
    String md5Hash;

    static FileDesc read(File f)
    {
        new FileDesc(
                name: f.name,
                path: f.parentFile.absolutePath,
                modified: new Date(f.lastModified()),
                md5Hash: Utils.generateMD5(f),
                length: f.length()
        );
    }

    static processFolder(File folder, Closure closure)
    {
        folder.eachFileRecurse(FileType.FILES)
                {
                    def d = FileDesc.read(it)
                    closure.call(d)
                }
    }
}
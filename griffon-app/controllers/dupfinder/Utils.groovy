package dupfinder

import java.security.MessageDigest

/**
 * @author koverg70
 */
class Utils
{
    final static int DIGEST_BUF = 16384*8;

    static Writable pogoToXml( object )
    {
        new groovy.xml.StreamingMarkupBuilder().bind {
            "${object.getClass().simpleName}" {
                object.getClass().declaredFields.grep { !it.synthetic }.name.each { n ->
                    "$n"( object."$n" )
                }
            }
        }
    }

    static String generateMD5(File f)
    {
        MessageDigest digest = MessageDigest.getInstance("MD5")
        try
        {
            RandomAccessFile raf = new RandomAccessFile(f, "r")
            try
            {
                byte[] buffer = new byte[DIGEST_BUF]

                // beginning of file
                int len = (int)([DIGEST_BUF, raf.length()].min())
                raf.read buffer, 0, len
                digest.update buffer, 0, len
                if (raf.length() >= DIGEST_BUF*3)
                {
                    // middle of file
                    raf.seek((long)((raf.length() - DIGEST_BUF) / 2))
                    raf.read buffer, 0, DIGEST_BUF;
                    digest.update buffer, 0, DIGEST_BUF
                }
                if (raf.length() >= DIGEST_BUF*2)
                {
                    // end of file
                    raf.seek raf.length() - DIGEST_BUF
                    raf.read buffer, 0, DIGEST_BUF
                    digest.update buffer, 0, DIGEST_BUF
                }
                new BigInteger(1, digest.digest()).toString(16).padLeft(32, '0')
            }
            finally
            {
                raf.close();
            }
        }
        catch (Exception)
        {
            // ignore it: e.g. java.io.FileNotFoundException (A m�velet v�grehajt�sa nem siker�lt, mert a f�jl v�rust vagy v�lhet�en nemk�v�natos szoftvert tartalmaz)
            return "########";
        }
    }

    static Collection<Collection<FileDesc>> computeDuplicates(Collection<FileDesc> fileDescriptors)
    {
        // duplicate maps: the key is the md5 hash code and the value is the list of duplicates
        // so the real duplicates has value list with more than one element
        fileDescriptors.groupBy{it.md5Hash}.findAll {it.value.size() > 1}.values()
    }

    static printDuplicates(Collection<FileDesc> descrs)
    {
        def df = new DecimalFormat("#,###,###,##0.00" )

        // duplicates
        println "Looking for duplicates..."

        def duplarr = computeDuplicates(descrs)

        println "Duplicates: " + duplarr.size

        duplarr = duplarr.sort false, {it[0].length}

        def size = 0

        println "Duplicates: "
        duplarr.each {
            println it[0].name + ": " + it.size() + " occurrences in ["
            it.each {
                println "\t\t" + it.path + "/" + it.name + "; "
                size += it.length
            }
            // size -= it.value[0].length
            println "]"
        }


        println df.format(size) + " bytes can be freed by removing duplicates"
    }

    public static String convertToStringRepresentation(final long value){
        final long K = 1024;
        final long M = K * K;
        final long G = M * K;
        final long T = G * K;

        final long[] dividers = [ T, G, M, K, 1 ];
        final String[] units = [ "TB", "GB", "MB", "KB", "B" ];
        if(value < 1)
            throw new IllegalArgumentException("Invalid file size: " + value);
        String result = null;
        for(int i = 0; i < dividers.length; i++){
            final long divider = dividers[i];
            if(value >= divider){
                result = format(value, divider, units[i]);
                break;
            }
        }
        return result;
    }

    private static String format(final long value,
                                 final long divider,
                                 final String unit){
        final double result =
                divider > 1 ? (double) value / (double) divider : (double) value;
        return String.format("%.1f %s", Double.valueOf(result), unit);
    }

}
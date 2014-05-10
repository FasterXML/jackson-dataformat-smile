package perf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;

public class ReadPerfMediaItem extends ReaderTestBase
{
    @Override
    protected int targetSizeMegs() { return 10; }

    public static void main(String[] args) throws Exception
    {
        if (args.length != 0) {
            System.err.println("Usage: java ...");
            System.exit(1);
        }
        SmileFactory sf = new SmileFactory();
        ObjectMapper m = new ObjectMapper(sf);

        byte[] smile = m.writeValueAsBytes(MediaItem.buildItem());

        new ReadPerfUntyped()
            .testFromBytes(
                m, "MediaItem-as-Smile1", smile, MediaItem.class
                ,m, "MediaItem-as-Smile2", smile, MediaItem.class
                );
    }
}

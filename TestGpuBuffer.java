import com.mojang.blaze3d.buffers.GpuBuffer;
public class TestGpuBuffer {
    public static void main(String[] args) {
        System.out.println("COPY_DST: " + GpuBuffer.USAGE_COPY_DST);
        System.out.println("UNIFORM: " + GpuBuffer.USAGE_UNIFORM);
        System.out.println("MAP_WRITE: " + GpuBuffer.USAGE_MAP_WRITE);
    }
}

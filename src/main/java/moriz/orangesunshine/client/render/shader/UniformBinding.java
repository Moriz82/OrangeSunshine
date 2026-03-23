package moriz.orangesunshine.client.render.shader;

import java.util.*;

import org.joml.Vector3f;

public interface UniformBinding {
    UniformBinding EMPTY = (uniforms, tickDelta, screenWidth, screenHeight, pass) -> {
    };

    void bindUniforms(UniformSetter uniforms, float tickDelta, int screenWidth, int screenHeight, Runnable pass);

    public interface UniformSetter {
        void set(String name, float value);

        void set(String name, float...values);

        void set(String name, Vector3f values);

        default boolean setIfNonZero(String name, float value) {
            set(name, value);
            return value > 0;
        }
    }

    /** Describes a single field in a std140 uniform block. */
    enum UboFieldType {
        FLOAT(1), VEC2(2), VEC3(3), VEC4(4), INT(1);

        final int components;

        UboFieldType(int components) {
            this.components = components;
        }
    }

    record UboField(String name, UboFieldType type) {}

    static UniformBinding.Set start() {
        return new Set();
    }

    final class Set {
        UniformBinding global = EMPTY;

        final Map<String, UniformBinding> programBindings = new HashMap<>();
        /** Maps "passKey.UboBlockName" → ordered list of fields. */
        final Map<String, List<UboField>> uboLayouts = new LinkedHashMap<>();

        public Set bind(UniformBinding all) {
            this.global = all;
            return this;
        }

        public Set program(String programName, UniformBinding binding) {
            programBindings.put(programName, binding);
            return this;
        }

        /**
         * Declare the UBO layout for a specific pass and block.
         *
         * @param passKey   the fragment shader name component (e.g. "heat_distortion")
         * @param blockName the std140 block name in the GLSL shader (e.g. "HeatDistortionConfig")
         * @param fields    ordered list of fields matching the block layout
         */
        public Set ubo(String passKey, String blockName, List<UboField> fields) {
            uboLayouts.put(passKey + "." + blockName, fields);
            return this;
        }
    }
}

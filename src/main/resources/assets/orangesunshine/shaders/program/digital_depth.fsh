#version 150
#moj_import <orangesunshine:random_from_vec.glsl>
#moj_import <orangesunshine:get_desaturated_color.glsl>
#moj_import <orangesunshine:reduce_pallete.glsl>
#moj_import <orangesunshine:pixelate.glsl>
#moj_import <orangesunshine:linear.glsl>

uniform sampler2D DepthSampler;

uniform vec2 depthRange;

float getPixelDensity(vec2 newUV, vec4 newColor) {
  float textureDepth = texture(DepthSampler, newUV).r;
  return linearize(textureDepth, depthRange.x, depthRange.y);
}

#moj_import <orangesunshine:apply_digitize.glsl>

void main() {
  apply_digitize();
}

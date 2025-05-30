#version 120

uniform sampler2D texture;
uniform vec2 resolution;
uniform float time;
uniform vec3 cameraPosition;
uniform float viewDistance;

varying vec2 texCoord;
varying vec3 worldPosition;

void main() {
    vec4 color = texture2D(texture, texCoord);

    // Glow-Basisfarbe (z. B. weißes Leuchten)
    vec3 glowColor = vec3(1.0, 1.0, 1.0);

    // Abstand von Kamera zum Pixel berechnen
    float dist = length(worldPosition - cameraPosition);

    // Glow-Falloff: Je weiter entfernt, desto schwächer
    float intensity = 1.0 / (dist * 0.25 + 1.0);  // skaliert aggressiv runter
    intensity = pow(intensity, 1.5);              // weich aber sichtbar

    // Kantenglättung vermeiden: harte Linie
    float edge = 0.95;
    if (color.a < edge) discard;

    // Output: kombinierter Glow + Originalfarbe (wenn nötig)
    gl_FragColor = vec4(glowColor * intensity, 1.0);
}

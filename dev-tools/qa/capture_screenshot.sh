#!/usr/bin/env bash
set -euo pipefail

OUT_FILE="${1:-/tmp/orangesunshine-screenshot.png}"
mkdir -p "$(dirname "$OUT_FILE")"
SNAPSHOT_DELAY="${SNAPSHOT_DELAY:-0}"

if ! [[ "${SNAPSHOT_DELAY}" =~ ^[0-9]+([.][0-9]+)?$ ]]; then
    echo "Invalid SNAPSHOT_DELAY value: ${SNAPSHOT_DELAY}" >&2
    exit 2
fi

if [[ "${SNAPSHOT_DELAY}" != "0" ]]; then
    sleep "${SNAPSHOT_DELAY}"
fi

if [[ -n "${SNAPSHOT_TOOL:-}" ]]; then
    case "$SNAPSHOT_TOOL" in
        grim)
            if command -v grim >/dev/null 2>&1; then
                grim "$OUT_FILE"
                echo "$OUT_FILE"
                exit 0
            fi
            ;;
        gnome)
            if command -v gnome-screenshot >/dev/null 2>&1; then
                gnome-screenshot -f "$OUT_FILE"
                echo "$OUT_FILE"
                exit 0
            fi
            ;;
        import)
            if command -v import >/dev/null 2>&1; then
            if [[ -z "${DISPLAY:-}" ]]; then
                echo "DISPLAY is not set for import tool" >&2
                exit 2
            fi
                import -window root "$OUT_FILE"
                echo "$OUT_FILE"
                exit 0
            fi
            ;;
        spectacle)
            if command -v spectacle >/dev/null 2>&1; then
                spectacle -b -n -o "$OUT_FILE" >/dev/null 2>&1
                echo "$OUT_FILE"
                exit 0
            fi
            ;;
        flameshot)
            if command -v flameshot >/dev/null 2>&1; then
                flameshot gui --raw > "$OUT_FILE"
                echo "$OUT_FILE"
                exit 0
            fi
            ;;
    esac
fi

if command -v grim >/dev/null 2>&1; then
    grim "$OUT_FILE"
    echo "$OUT_FILE"
    exit 0
fi

if command -v gnome-screenshot >/dev/null 2>&1; then
    gnome-screenshot -f "$OUT_FILE"
    echo "$OUT_FILE"
    exit 0
fi

if command -v import >/dev/null 2>&1; then
    if [[ -z "${DISPLAY:-}" ]]; then
        echo "DISPLAY is not set; ImageMagick import cannot run" >&2
        exit 2
    fi
    import -window root "$OUT_FILE"
    echo "$OUT_FILE"
    exit 0
fi

if command -v spectacle >/dev/null 2>&1; then
    spectacle -b -n -o "$OUT_FILE"
    echo "$OUT_FILE"
    exit 0
fi

if command -v flameshot >/dev/null 2>&1; then
    flameshot gui --raw > "$OUT_FILE"
    echo "$OUT_FILE"
    exit 0
fi

echo "No screenshot tool available. Install one of: grim, gnome-screenshot, import, spectacle, flameshot." >&2
exit 1

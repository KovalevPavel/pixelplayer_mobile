#!/usr/bin/env python3

from __future__ import annotations

import re
import shutil
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
SRC_DIR = ROOT / "docs" / "wiki-src"
DIST_DIR = ROOT / "docs" / "wiki-dist"

PAGES = [
    "Home",
    "Product Overview",
    "Getting Started",
    "Architecture Overview",
    "Module Guide",
    "Features",
    "Networking and Data Flow",
    "Playback",
    "Localization and Settings",
    "Development Notes",
]

NAV_PAGES = [
    "Home",
    "Getting Started",
    "Architecture Overview",
    "Features",
]


def slugify(page_name: str) -> str:
    return page_name.replace(" ", "-")


def build_nav_block() -> str:
    links = " | ".join(f"[{page}]({slugify(page)})" for page in NAV_PAGES)
    return f"> {links}\n\n"


def normalize_links(content: str) -> str:
    def replace(match: re.Match[str]) -> str:
        label = match.group(1)
        target = match.group(2).strip()

        if "://" in target or target.startswith("#"):
            return match.group(0)

        target = target.replace("\\", "/")
        target = target.removeprefix("./")

        if target.endswith(".md"):
            target = target[:-3]

        if "/" in target:
            return f"[{label}]({target})"

        return f"[{label}]({slugify(target)})"

    return re.sub(r"\[([^\]]+)\]\(([^)]+)\)", replace, content)


def generate_sidebar() -> str:
    items = "\n".join(f"- [{page}]({slugify(page)})" for page in PAGES)
    return f"# Pages\n\n{items}\n"


def generate_footer() -> str:
    return (
        "---\n"
        "Published from repository sources in `docs/wiki-src`.\n"
    )


def main() -> None:
    if not SRC_DIR.exists():
        raise SystemExit(f"Missing source directory: {SRC_DIR}")

    if DIST_DIR.exists():
        shutil.rmtree(DIST_DIR)
    DIST_DIR.mkdir(parents=True, exist_ok=True)

    nav_block = build_nav_block()

    for page in PAGES:
        source_file = SRC_DIR / f"{page}.md"
        if not source_file.exists():
            raise SystemExit(f"Missing source page: {source_file}")

        raw = source_file.read_text(encoding="utf-8").strip()
        normalized = normalize_links(raw)
        output = nav_block + normalized + "\n"
        (DIST_DIR / f"{page}.md").write_text(output, encoding="utf-8")

    (DIST_DIR / "_Sidebar.md").write_text(generate_sidebar(), encoding="utf-8")
    (DIST_DIR / "_Footer.md").write_text(generate_footer(), encoding="utf-8")


if __name__ == "__main__":
    main()

package com.cool.request.utils;

public class Version implements Comparable<Version> {
    private String version;

    public Version(String version) {
        this.version = version;
    }

    @Override
    public int compareTo(Version other) {
        String[] thisParts = this.version.split("\\.");
        String[] otherParts = other.version.split("\\.");

        int maxLength = Math.max(thisParts.length, otherParts.length);
        for (int i = 0; i < maxLength; i++) {
            int thisPart = i < thisParts.length ? Integer.parseInt(thisParts[i]) : 0;
            int otherPart = i < otherParts.length ? Integer.parseInt(otherParts[i]) : 0;

            if (thisPart < otherPart) {
                return -1;
            }
            if (thisPart > otherPart) {
                return 1;
            }
        }
        return 0;
    }

}

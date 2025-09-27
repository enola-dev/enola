package dev.enola.be;

import dev.enola.common.exec.ExecRequest;

public class Be {
    public static void main(String[] args) {
        ExecRequest.newBuilder();
        System.out.println("Hi!");
        // System.exit(cli(args).execute());
    }
}

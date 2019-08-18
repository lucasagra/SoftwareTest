package br.ufal.ic.academico.view;

import br.ufal.ic.academico.control.Format;

public class Menu {

    public int main() {
        System.out.println("[1] - Enroll\n" +
                "[2] - Secretaries\n" +
                "[3] - Subjects\n" +
                "[4] - Students\n" +
                "[0] - Exit\n");

        int option = new Format().inputSelect(0, 4);
        System.out.println();

        return option;
    }
}

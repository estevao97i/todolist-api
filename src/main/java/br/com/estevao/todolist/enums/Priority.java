package br.com.estevao.todolist.enums;

public enum Priority {
    HIGH("High"),
    MEDIUM("medium"),
    LOW("Low");

    private String desc;

    Priority(String desc) {
        this.desc = desc;
    }
}

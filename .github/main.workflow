workflow "Test" {
  on = "push"
  resolves = ["JDK 8", "JDK 11"]
}

action "JDK 8" {
  uses = "docker://openjdk:8"
  runs = "./gradlew assemble"
}

action "JDK 11" {
  uses = "docker://openjdk:11"
  runs = "./gradlew assemble"
}

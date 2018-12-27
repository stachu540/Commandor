workflow "Test" {
  on = "push"
  resolves = ["JDK 8", "JDK 11"]
}

action "JDK 8" {
  uses = "docker://gradle:jdk8"
  runs = "gradle --no-daemon assemble"
}

action "JDK 11" {
  uses = "docker://gradle:jdk11"
  runs = "gradle --no-daemon assemble"
}

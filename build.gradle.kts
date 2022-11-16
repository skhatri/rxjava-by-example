plugins {
  id("java")
  id("idea")
}

dependencies {
  implementation("org.slf4j:slf4j-api:1.7.36")
  implementation("org.slf4j:log4j-over-slf4j:1.7.36")
  implementation("ch.qos.logback:logback-classic:1.4.4")

  implementation("io.reactivex.rxjava3:rxjava:3.1.5")
  implementation("io.projectreactor.addons:reactor-adapter:3.4.8")
  implementation("org.springframework:spring-webflux:5.3.23")
  implementation("org.springframework.boot:spring-boot-starter-webflux:2.7.5")

}

repositories {
  mavenCentral()
}



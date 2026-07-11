# TheosferaProtocol — Project State

Última actualización: 11 de julio de 2026.

## 1. Propósito del repositorio

`TheosferaProtocol` es la biblioteca Java compartida que define el contrato de comunicación entre los componentes de la network Theosfera.

Consumidores previstos:

- `TheosferaCore`, ejecutado en servidores Paper.
- `TheosferaProxy`, ejecutado en Velocity.
- Futuros plugins de modalidades que necesiten participar en el protocolo.

La biblioteca debe permanecer independiente de plataforma. No puede contener dependencias, imports ni lógica específica de Paper, Bukkit, Velocity, Redis o una modalidad concreta.

## 2. Alcance arquitectónico confirmado

La primera etapa de la network tendrá:

- Proxy.
- Auth.
- Lobby.
- Skyblock.

El transporte inicial será Plugin Messaging mediante el canal:

```text
theosfera:network
```

Redis podrá añadirse posteriormente como transporte en tiempo real, sin duplicar los modelos ni alterar el contrato público de mensajes.

Reglas generales del protocolo:

- JSON codificado en UTF-8.
- Versión inicial `1`.
- Tamaño máximo estricto de 32 KiB por mensaje.
- `requestId` para correlación e idempotencia.
- Timeout contractual inicial de 3 segundos para solicitudes.
- Los mensajes recibidos mediante Velocity deben marcarse como manejados inmediatamente después de confirmar el canal.
- Debe validarse que el origen sea una conexión de servidor autorizada.
- Auth tendrá restricciones especiales y no será tratado como un backend confiable para cualquier tipo de mensaje.

## 3. Tecnología

- Java 21.
- Gradle Kotlin DSL.
- Plugin Gradle `java-library`.
- Gson para JSON.
- JUnit 5 para pruebas.
- Paquete raíz: `com.theosfera.protocol`.
- Artefacto actual: `TheosferaProtocol-0.1.0-SNAPSHOT.jar`.

El proyecto genera también un `sourcesJar`.

## 4. Componentes implementados y fusionados

### 4.1. Fundación de biblioteca

El template original de Paper fue convertido en una biblioteca Java pura.

Se eliminaron:

- La dependencia de Paper.
- La clase `JavaPlugin` del template.
- `plugin.yml`.

Se configuraron Gson, JUnit 5, Java 21 y el lanzador de JUnit Platform requerido por Gradle 9.

### 4.2. `ProtocolVersion`

Ruta:

```text
src/main/java/com/theosfera/protocol/ProtocolVersion.java
```

Define la versión vigente:

```java
ProtocolVersion.CURRENT == 1
```

Tiene una prueba unitaria que protege este valor inicial.

### 4.3. `ProtocolEnvelope<T>`

Ruta:

```text
src/main/java/com/theosfera/protocol/message/ProtocolEnvelope.java
```

Sobre genérico compartido por todos los mensajes. Campos:

- `version`.
- `type`.
- `requestId`.
- `timestamp`.
- `payload`.

Incluye una factoría `create(type, payload)` que usa:

- La versión vigente.
- Un UUID aleatorio.
- La hora actual en milisegundos.

Validaciones actuales:

- La versión debe coincidir con `ProtocolVersion.CURRENT`.
- El tipo no puede ser nulo y se normaliza con `trim()`.
- El tipo debe estar en `UPPER_SNAKE_CASE` y ocupar como máximo 64 caracteres.
- `requestId` no puede ser nulo.
- `timestamp` debe ser mayor que cero.
- `payload` no puede ser nulo.

### 4.4. Codec JSON

Rutas:

```text
src/main/java/com/theosfera/protocol/codec/ProtocolJsonCodec.java
src/main/java/com/theosfera/protocol/codec/ProtocolCodecException.java
```

`ProtocolJsonCodec`:

- Codifica `ProtocolEnvelope<?>` a `byte[]` JSON UTF-8.
- Decodifica un `byte[]` a un envelope con payload tipado.
- Expone `MAX_MESSAGE_BYTES = 32 * 1024`.
- Rechaza envelopes nulos.
- Rechaza mensajes nulos o vacíos.
- Rechaza mensajes superiores a 32 KiB tanto al codificar como al decodificar.
- Usa un decodificador UTF-8 estricto.
- Rechaza secuencias UTF-8 malformadas.
- Rechaza JSON inválido y el valor JSON `null`.
- Encapsula los errores mediante `ProtocolCodecException`.

Las pruebas cubren ida y vuelta, Unicode, nulidad, JSON malformado, UTF-8 inválido y límites de tamaño.

### 4.5. Catálogo de tipos

Ruta:

```text
src/main/java/com/theosfera/protocol/message/ProtocolMessageType.java
```

Tipos iniciales fusionados:

```text
BACKEND_HELLO
BACKEND_HELLO_ACK
PLAYER_AUTHENTICATED
PLAYER_SERVER_READY
TRANSFER_REQUEST
TRANSFER_RESULT
PING
PONG
```

El catálogo ofrece:

- Constantes públicas de texto.
- `isKnown(String)` para comprobar tipos vigentes.
- `knownTypes()` como conjunto inmutable.

Decisión importante: `ProtocolEnvelope.type` permanece como `String`, no como `enum`, para permitir que consumidores antiguos reciban tipos futuros y los rechacen de forma controlada sin romper la deserialización.

## 5. Verificación realizada

Todas las etapas fusionadas fueron verificadas con:

```powershell
git diff --cached --check
.\gradlew.bat build --no-daemon
```

Estado comprobado:

- Compilación exitosa.
- Pruebas unitarias exitosas.
- Sin dependencias de Paper o Velocity.
- Sin metadata de plugin.
- Sin archivos de compilación versionados.

Los repositorios fueron movidos fuera de OneDrive a:

```text
C:\Theosfera\Plugins
```

Se eliminaron las cachés locales antiguas `build` y `.gradle` de los proyectos para descartar referencias a la ruta anterior.

## 6. Historial funcional fusionado

Ya están fusionadas en `main` estas unidades de trabajo:

1. Fundación Java pura de `TheosferaProtocol`.
2. Envelope genérico validado.
3. Codec JSON UTF-8 con límite de 32 KiB.
4. Catálogo inicial de tipos de mensajes.

No se debe repetir ninguna de estas tareas.

## 7. Estado de ramas al crear este checkpoint

- Rama principal actualizada: `main`.
- Rama documental actual: `docs/protocol-foundation-checkpoint`.
- Rama futura ya creada y publicada: `feature/protocol-handshake-heartbeat`.

La rama `feature/protocol-handshake-heartbeat` se creó desde `main` antes del checkpoint. Después de fusionar este documento, debe actualizarse con el nuevo `main` antes de implementar código.

## 8. Próximo paso exacto

Implementar los payloads tipados de handshake y heartbeat:

- `BackendType` con los valores iniciales `AUTH`, `LOBBY` y `SKYBLOCK`.
- `BackendHelloPayload`.
- `BackendHelloAckPayload`.
- `PingPayload`.
- `PongPayload`.

Cada modelo debe:

- Permanecer independiente de plataforma.
- Ser inmutable, preferiblemente mediante `record`.
- Validar sus invariantes en el constructor canónico.
- Tener pruebas unitarias de casos válidos e inválidos.
- Tener pruebas de ida y vuelta mediante `ProtocolJsonCodec`.

Después del handshake y heartbeat se implementarán, en incrementos separados:

1. Payloads de autenticación y preparación del jugador.
2. Payloads de solicitudes y resultados de transferencia.
3. Registro seguro que relacione cada tipo con su clase de payload.
4. Integración de la biblioteca en `TheosferaProxy`.
5. Integración de la biblioteca en `TheosferaCore`.

## 9. Acciones que todavía no corresponden

No implementar todavía:

- Transporte Plugin Messaging dentro de esta biblioteca.
- Redis.
- Persistencia en base de datos.
- Lógica de parties, escuadrones o perfiles.
- APIs de Paper, Bukkit o Velocity.
- Reglas específicas de Skyblock.
- Integración directa de Core y Proxy antes de terminar los modelos y el registro tipado.

## 10. Flujo para fusionar este checkpoint

Después de añadir este archivo a la raíz:

```powershell
git add PROJECT_STATE.md
git diff --cached --check
git status
git commit -m "docs: add protocol foundation checkpoint"
git push
```

Abrir un Pull Request hacia `main`, esperar los checks y usar `Squash and merge`.

## 11. Cómo retomar después del checkpoint

Después de fusionar el PR documental:

```powershell
cd C:\Theosfera\Plugins\TheosferaProtocol
git switch main
git pull origin main
git branch -d docs/protocol-foundation-checkpoint
git fetch --prune
git switch feature/protocol-handshake-heartbeat
git merge main
git push
git status
```

La fusión de `main` en la rama futura solo incorporará el checkpoint documental. Si se prefiere mantener historia lineal antes de comenzar a trabajar y la rama continúa vacía, también puede recrearse desde `main`.

Al continuar en otro chat, indicar:

> Continúa TheosferaProtocol desde el repositorio real. Revisa primero AGENTS.md, PROJECT_STATE.md y CONTRIBUTING.md. No repitas la fundación, ProtocolEnvelope, ProtocolJsonCodec ni ProtocolMessageType. La siguiente tarea es implementar los payloads de handshake y heartbeat en la rama feature/protocol-handshake-heartbeat.


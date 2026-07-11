# TheosferaProtocol — Project State

Última actualización: 11 de julio de 2026.

## 1. Propósito del repositorio

`TheosferaProtocol` es la biblioteca Java compartida que define el contrato de comunicación entre los componentes de la network Theosfera.

Consumidores previstos:

- `TheosferaCore`, ejecutado en servidores Paper.
- `TheosferaProxy`, ejecutado en Velocity.
- Futuros plugins de modalidades que necesiten participar en el protocolo.

La biblioteca permanece independiente de plataforma. No puede contener dependencias, imports ni lógica específica de Paper, Bukkit, Velocity, Redis o una modalidad concreta.

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
- Rechaza mensajes superiores a 32 KiB al codificar y decodificar.
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

`ProtocolEnvelope.type` permanece como `String`, no como `enum`, para permitir que consumidores antiguos reciban tipos futuros y los rechacen controladamente sin romper la deserialización.

### 4.6. Payloads de handshake y heartbeat

Paquete:

```text
src/main/java/com/theosfera/protocol/message/payload
```

Modelos fusionados:

- `BackendType`.
- `BackendHelloPayload`.
- `BackendHelloAckPayload`.
- `PingPayload`.
- `PongPayload`.

`BackendType` contiene:

```text
AUTH
LOBBY
SKYBLOCK
```

`BackendHelloPayload` contiene:

- `backendName`.
- `backendType`.

El nombre de backend:

- Se normaliza con `trim()`.
- Debe comenzar con una letra o número.
- Solo admite letras, números, guiones y guiones bajos.
- Tiene un máximo de 64 caracteres.

`BackendHelloAckPayload` contiene:

- `accepted`.
- `message`.

El mensaje:

- No puede ser nulo ni vacío.
- Se normaliza con `trim()`.
- Tiene un máximo de 256 caracteres.

`PingPayload` contiene:

- `sentAt`.

`PongPayload` contiene:

- `pingSentAt`.
- `respondedAt`.

Los timestamps deben ser mayores que cero y la respuesta no puede ser anterior al ping.

### 4.7. Payloads del ciclo de vida del jugador

Modelos fusionados:

- `PlayerAuthenticatedPayload`.
- `PlayerServerReadyPayload`.

`PlayerAuthenticatedPayload` contiene:

- `playerId`.
- `playerName`.
- `authenticatedAt`.

El nombre del jugador:

- Se normaliza con `trim()`.
- Debe contener entre 3 y 16 caracteres.
- Solo admite letras, números y guiones bajos.

`PlayerServerReadyPayload` contiene:

- `playerId`.
- `backendName`.
- `readyAt`.

El nombre del backend utiliza las mismas restricciones definidas para el handshake.

Los UUID no pueden ser nulos y los timestamps deben ser mayores que cero.

### 4.8. Payloads de transferencia

Modelos fusionados:

- `TransferRequestPayload`.
- `TransferResultPayload`.
- `TransferResultStatus`.

`TransferRequestPayload` contiene:

- `playerId`.
- `targetBackendType`.

El destino usa un tipo lógico para permitir que el proxy elija una instancia concreta. `AUTH` no puede utilizarse como destino de una transferencia normal.

`TransferResultStatus` contiene:

```text
SUCCESS
REJECTED
FAILED
TIMED_OUT
```

`TransferResultPayload` contiene:

- `playerId`.
- `status`.
- `message`.

El mensaje no puede ser nulo ni vacío, se normaliza con `trim()` y tiene un máximo de 256 caracteres.

La correlación entre solicitud y resultado utiliza el `requestId` del envelope y no se duplica en los payloads.

### 4.9. Registro tipado de mensajes

Ruta:

```text
src/main/java/com/theosfera/protocol/message/ProtocolMessageRegistry.java
```

El registro relaciona cada tipo conocido con su clase exacta de payload:

```text
BACKEND_HELLO          -> BackendHelloPayload
BACKEND_HELLO_ACK      -> BackendHelloAckPayload
PLAYER_AUTHENTICATED   -> PlayerAuthenticatedPayload
PLAYER_SERVER_READY    -> PlayerServerReadyPayload
TRANSFER_REQUEST       -> TransferRequestPayload
TRANSFER_RESULT        -> TransferResultPayload
PING                    -> PingPayload
PONG                    -> PongPayload
```

El registro ofrece:

- `payloadType(String)` mediante `Optional<Class<?>>`.
- `isRegistered(String)`.
- `registeredTypes()` como conjunto inmutable.

Tipos nulos, desconocidos o incorrectamente normalizados no se registran.

Una prueba exige que `ProtocolMessageType.knownTypes()` y `ProtocolMessageRegistry.registeredTypes()` coincidan exactamente. Si se añade un tipo futuro sin registrar su payload, el build falla.

El registro no confía en nombres de clases recibidos externamente.

## 5. Pruebas implementadas

Las pruebas unitarias cubren:

- Creación válida de cada modelo.
- Nulidad de campos obligatorios.
- Strings vacíos después de normalización.
- Caracteres inválidos.
- Longitudes mínimas y máximas.
- Timestamps iguales a cero o negativos.
- Orden temporal de ping y pong.
- Restricción de `AUTH` como destino de transferencia.
- Todos los estados de transferencia.
- Tipos desconocidos y nulos en el registro.
- Inmutabilidad de los conjuntos públicos.
- Sincronización entre catálogo y registro.
- Ida y vuelta JSON de todos los payloads mediante `ProtocolJsonCodec`.

Pruebas de codec agrupadas:

- `HandshakeHeartbeatCodecTest`.
- `PlayerLifecycleCodecTest`.
- `TransferCodecTest`.

## 6. Verificación realizada

Todos los incrementos fueron verificados antes de fusionarse con:

```powershell
git diff --cached --check
.\gradlew.bat clean build --no-daemon
```

Estado comprobado:

- Compilación exitosa.
- Pruebas unitarias exitosas.
- Sin dependencias de Paper o Velocity.
- Sin metadata de plugin.
- Sin archivos de compilación versionados.
- Cambios implementados mediante ramas y Pull Requests.
- Árbol de trabajo limpio después de cada fusión.

Los repositorios se encuentran fuera de OneDrive en:

```text
C:\Theosfera\Plugins
```

## 7. Historial funcional fusionado

Ya están fusionadas en `main` estas unidades de trabajo:

1. Fundación Java pura de `TheosferaProtocol`.
2. Envelope genérico validado.
3. Codec JSON UTF-8 con límite de 32 KiB.
4. Catálogo inicial de tipos de mensajes.
5. Payloads de handshake y heartbeat.
6. Payloads de autenticación y preparación del jugador.
7. Payloads de solicitudes y resultados de transferencia.
8. Registro seguro de tipos y clases de payload.

No se debe repetir ninguna de estas tareas.

## 8. Estado de ramas al crear este checkpoint

- Rama principal actualizada: `main`.
- Rama documental actual: `docs/protocol-contract-checkpoint`.
- Las ramas funcionales anteriores fueron fusionadas mediante Pull Requests y pueden eliminarse localmente y podarse del remoto.

Este checkpoint no modifica el contrato ni añade nuevas funcionalidades.

## 9. Próximo paso exacto

Comenzar la integración de `TheosferaProtocol` en `TheosferaProxy`.

La integración debe realizarse dentro del repositorio `TheosferaProxy`, en una rama nueva creada desde su `main` actualizado.

Primer alcance previsto:

1. Añadir `TheosferaProtocol` como dependencia de compilación.
2. Decidir y documentar el mecanismo de consumo local durante desarrollo.
3. Registrar el canal de Plugin Messaging:

   ```text
   theosfera:network
   ```

4. Crear componentes separados para:

    - Recepción de mensajes.
    - Validación de canal y origen.
    - Decodificación segura.
    - Despacho por tipo.
    - Envío de respuestas.

5. Mantener la clase principal de Velocity enfocada en ciclo de vida y composición.
6. Marcar los mensajes de Velocity como manejados inmediatamente después de confirmar el canal.
7. Aceptar mensajes únicamente desde conexiones de servidor autorizadas.
8. Aplicar restricciones especiales a mensajes originados desde Auth.
9. No implementar todavía Redis, persistencia ni sistemas sociales.

Antes de escribir código en `TheosferaProxy` se deben revisar:

- `AGENTS.md`.
- `PROJECT_STATE.md`, si existe.
- `CONTRIBUTING.md`.
- `build.gradle.kts`.
- La clase principal actual.
- La estructura de paquetes.
- El estado de Git y la rama vigente.

## 10. Acciones que todavía no corresponden

No implementar todavía dentro de `TheosferaProtocol`:

- Transporte Plugin Messaging.
- APIs de Paper, Bukkit o Velocity.
- Redis.
- Persistencia en base de datos.
- Lógica de parties, friends, escuadrones o perfiles.
- Reglas específicas de Skyblock.
- Selección concreta de instancias backend.
- Ejecución real de transferencias.
- Autenticación real de jugadores.
- Decodificación automática mezclada con transporte.

La biblioteca debe seguir siendo un contrato Java puro.

## 11. Flujo para fusionar este checkpoint

Después de reemplazar este archivo:

```powershell
git add PROJECT_STATE.md
git diff --cached --check
git status
git commit -m "docs: add protocol contract checkpoint"
git push
```

Abrir un Pull Request hacia `main`, esperar los checks y usar **Squash and merge**.

## 12. Cómo retomar después del checkpoint

Después de fusionar el PR documental:

```powershell
cd C:\Theosfera\Plugins\TheosferaProtocol
git switch main
git pull origin main
git branch -d docs/protocol-contract-checkpoint
git fetch --prune
git status
```

Después cambiar al repositorio de Proxy:

```powershell
cd C:\Theosfera\Plugins\TheosferaProxy
git switch main
git pull origin main
git status
```

Crear una rama nueva para la integración únicamente después de revisar la documentación y estructura real de `TheosferaProxy`.

Al continuar en otro chat, indicar:

> Continúa la integración de TheosferaProtocol en TheosferaProxy desde los repositorios reales. Revisa primero AGENTS.md, PROJECT_STATE.md, CONTRIBUTING.md, build.gradle.kts y la clase principal de TheosferaProxy. El contrato inicial de TheosferaProtocol ya está completo y fusionado; no repitas sus modelos, codec, catálogo ni registro.
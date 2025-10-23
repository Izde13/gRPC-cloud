# Taller gRPC - Sistema de Saludo Remoto en Azure

## Descripción del Proyecto

Implementación de un servicio de saludo remoto utilizando gRPC con Protocol Buffers desplegado en Azure VM. El sistema permite enviar saludos personalizados y mantiene un contador persistente.

## Objetivos Cumplidos

- Implementar servicio gRPC en Java
- Definir servicios con Protocol Buffers
- Generar código Java automáticamente desde .proto
- Desplegar servidor en Azure VM
- Implementar cliente gRPC remoto
- Mantener persistencia de estado

## Arquitectura del Sistema

**Servidor gRPC (Cloud)**
- Ubicación: Azure VM Ubuntu 22.04
- Java: OpenJDK 17
- Framework: gRPC 1.58.0 + Protobuf 3.24.0
- Puerto: 50051

**Cliente gRPC (Local)**
- Conexión: TCP/HTTP2 a IP pública Azure
- Interfaz: Consola interactiva

## Componentes del Código

### Servidor

**greeter.proto** - Define el contrato del servicio

Mensajes:
- `HelloRequest`: nombre del usuario
- `HelloReply`: mensaje de saludo
- `Empty`: mensaje vacío
- `CountReply`: contador de saludos

Servicios:
- `SayHello`: envía saludo personalizado
- `GetGreetingCount`: retorna contador total

**GreeterServiceImpl.java** - Implementación del servicio
- Lógica de negocio RPC
- Contador atómico persistente
- Usa StreamObserver para respuestas

**GreeterServer.java** - Servidor principal
- Inicia servidor en puerto 50051
- Registra servicio
- Shutdown hook para apagado seguro

### Cliente

**GreeterClient.java** - Cliente remoto
- Crea canal gRPC
- Genera stub para invocar métodos
- Construye mensajes Protocol Buffers
- Menú interactivo

## Configuración en Azure

**Regla NSG adicional:**

| Nombre | Puerto | Protocolo |
|--------|--------|-----------|
| Allow-gRPC | 50051 | TCP |

**Servicio systemd:** `grpc-greeter.service`

## Instrucciones de Uso

### Servidor (Azure)
```bash
# Ver estado
sudo systemctl status grpc-greeter

# Ver logs
sudo journalctl -u grpc-greeter -f

# Reiniciar
sudo systemctl restart grpc-greeter
```

### Cliente (Local)
```bash
# Compilar
cd grpc-client
mvn clean compile

# Ejecutar
mvn exec:java -Dexec.mainClass="com.ucatolica.grpc.GreeterClient"
```

**Uso:**
1. Ingresar IP pública de Azure
2. Seleccionar opción del menú
3. Enviar saludo o consultar contador

## Ejemplo de Flujo

Usuario 1:
- Envía saludo "María" → "¡Hola, María! Este es el saludo número 1"
- Consulta contador → 1

Usuario 2 (otra máquina):
- Consulta contador → 1 (persistió)
- Envía saludo "Juan" → "¡Hola, Juan! Este es el saludo número 2"

## Información de Conexión

Endpoint: `[IP_PUBLICA]:50051`

Verificar:
```bash
telnet [IP_PUBLICA] 50051
```

## Solución de Problemas

**UNAVAILABLE: io exception**
- Verificar servicio: `sudo systemctl status grpc-greeter`
- Verificar NSG en Azure
- Verificar firewall: `sudo ufw status`

**Package GreeterGrpc does not exist**
```bash
mvn clean compile
```

**Failed to generate code**
- Verificar archivo: `src/main/proto/greeter.proto`
- Ejecutar: `mvn protobuf:compile`

## Conceptos Técnicos

**gRPC**
- Framework RPC moderno
- HTTP/2 como transporte
- Protocol Buffers para serialización

**Protocol Buffers**
- IDL independiente del lenguaje
- Generación automática de código
- Serialización binaria eficiente

**Diferencias con RMI**

| Característica | RMI | gRPC |
|----------------|-----|------|
| Lenguaje | Solo Java | Multi-lenguaje |
| Protocolo | Propietario | HTTP/2 |
| Serialización | Java | Protobuf |
| Definición | Interfaces Java | .proto |

## Conclusiones

- gRPC es la evolución moderna de RPC
- Protocol Buffers permite interoperabilidad
- HTTP/2 mejora eficiencia
- Generación automática de código reduce errores
- Arquitectura escalable para microservicios

## Referencias

- https://grpc.io/docs/languages/java/
- https://protobuf.dev/
- https://grpc.io/docs/what-is-grpc/core-concepts/

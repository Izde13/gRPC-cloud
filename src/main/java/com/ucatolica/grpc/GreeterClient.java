package com.ucatolica.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class GreeterClient {
    
    private final ManagedChannel channel;
    private final GreeterGrpc.GreeterBlockingStub blockingStub;
    
    public GreeterClient(String host, int port) {
        // Crear canal de comunicación
        channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        
        // Crear stub (proxy del cliente)
        blockingStub = GreeterGrpc.newBlockingStub(channel);
    }
    
    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }
    
    public void greet(String name) {
        System.out.println("\n--- Enviando saludo para: " + name + " ---");
        
        HelloRequest request = HelloRequest.newBuilder()
                .setName(name)
                .build();
        
        HelloReply response = blockingStub.sayHello(request);
        
        System.out.println("✓ Respuesta del servidor: " + response.getMessage());
    }
    
    public void getGreetingCount() {
        System.out.println("\n--- Consultando contador de saludos ---");
        
        Empty request = Empty.newBuilder().build();
        CountReply response = blockingStub.getGreetingCount(request);
        
        System.out.println("✓ Total de saludos realizados: " + response.getCount());
    }
    
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=== Cliente gRPC Greeter ===");
        System.out.print("Ingresa la IP del servidor gRPC: ");
        String serverIP = scanner.nextLine();
        
        GreeterClient client = new GreeterClient(serverIP, 50051);
        
        try {
            System.out.println("✓ Conectado al servidor gRPC en " + serverIP + ":50051\n");
            
            boolean continuar = true;
            
            while (continuar) {
                System.out.println("\n--- Menú ---");
                System.out.println("1. Enviar saludo");
                System.out.println("2. Ver contador de saludos");
                System.out.println("3. Salir");
                System.out.print("Selecciona una opción: ");
                
                int opcion = scanner.nextInt();
                scanner.nextLine(); // Consumir newline
                
                switch (opcion) {
                    case 1:
                        System.out.print("Ingresa tu nombre: ");
                        String nombre = scanner.nextLine();
                        client.greet(nombre);
                        break;
                        
                    case 2:
                        client.getGreetingCount();
                        break;
                        
                    case 3:
                        continuar = false;
                        System.out.println("¡Hasta luego!");
                        break;
                        
                    default:
                        System.out.println("✗ Opción inválida");
                }
            }
            
        } finally {
            client.shutdown();
            scanner.close();
        }
    }
}
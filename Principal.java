import com.google.gson.Gson;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Scanner;


public class Principal {
    public static void main(String[] args) {
        Scanner lectura = new Scanner(System.in);
        ConsultarMoneda consulta = new ConsultarMoneda();


        Moneda monedas = consulta.buscarMoneda("USD");


        if (monedas == null) {
            System.out.println("No se pudo obtener la información de la API. Verifica tu API Key o tu conexión.");
            return;
        }

        int opcion = 0;
        while (opcion != 7) {
            System.out.println("\n*************************************************");
            System.out.println("Sea bienvenido/a al Conversor de Moneda =]");
            System.out.println("1) Dólar => Peso argentino");
            System.out.println("2) Peso argentino => Dólar");
            System.out.println("3) Dólar => Real brasileño");
            System.out.println("4) Real brasileño => Dólar");
            System.out.println("5) Dólar => Peso colombiano");
            System.out.println("6) Peso colombiano => Dólar");
            System.out.println("7) Salir");
            System.out.println("Elija una opción válida:");
            System.out.println("*************************************************");

            try {
                opcion = Integer.parseInt(lectura.nextLine());

                if (opcion == 7) {
                    System.out.println("Cerrando el programa. ¡Gracias por usar nuestros servicios!");
                    break;
                }

                if (opcion < 1 || opcion > 7) {
                    System.out.println("Opción no válida, intente de nuevo.");
                    continue;
                }

                System.out.println("Ingrese el valor que desea convertir:");

                String entradaCantidad = lectura.nextLine().replace(",", ".");
                double cantidad = Double.parseDouble(entradaCantidad);
                double resultado = 0;


                double tasaARS = monedas.conversion_rates().get("ARS");
                double tasaBRL = monedas.conversion_rates().get("BRL");
                double tasaCOP = monedas.conversion_rates().get("COP");

                switch (opcion) {
                    case 1: // USD a ARS
                        resultado = cantidad * tasaARS;
                        System.out.printf("El valor %.2f [USD] corresponde al valor final de =>>> %.2f [ARS]\n", cantidad, resultado);
                        break;
                    case 2: // ARS a USD
                        resultado = cantidad / tasaARS;
                        System.out.printf("El valor %.2f [ARS] corresponde al valor final de =>>> %.2f [USD]\n", cantidad, resultado);
                        break;
                    case 3: // USD a BRL
                        resultado = cantidad * tasaBRL;
                        System.out.printf("El valor %.2f [USD] corresponde al valor final de =>>> %.2f [BRL]\n", cantidad, resultado);
                        break;
                    case 4: // BRL a USD
                        resultado = cantidad / tasaBRL;
                        System.out.printf("El valor %.2f [BRL] corresponde al valor final de =>>> %.2f [USD]\n", cantidad, resultado);
                        break;
                    case 5: // USD a COP
                        resultado = cantidad * tasaCOP;
                        System.out.printf("El valor %.2f [USD] corresponde al valor final de =>>> %.2f [COP]\n", cantidad, resultado);
                        break;
                    case 6: // COP a USD
                        resultado = cantidad / tasaCOP;
                        System.out.printf("El valor %.2f [COP] corresponde al valor final de =>>> %.2f [USD]\n", cantidad, resultado);
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Por favor, ingrese un número válido.");
            } catch (Exception e) {
                System.out.println("Ocurrió un error inesperado: " + e.getMessage());
            }
        }
        lectura.close();
    }
}




record Moneda(String base_code, Map<String, Double> conversion_rates) {
}


class ConsultarMoneda {
    public Moneda buscarMoneda(String monedaBase) {

        String apiKey = System.getenv("EXCHANGE_RATE_API_KEY")

        URI direccion = URI.create("https://v6.exchangerate-api.com/v6/" + apiKey + "/latest/" + monedaBase);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(direccion)
                .build();

        try {
            HttpResponse<String> response = client
                    .send(request, HttpResponse.BodyHandlers.ofString());

            // Convertimos la respuesta JSON a objeto Java usando Gson
            return new Gson().fromJson(response.body(), Moneda.class);

        } catch (Exception e) {
            System.out.println("Error al conectar con la API: " + e.getMessage());
            return null;
        }
    }
}
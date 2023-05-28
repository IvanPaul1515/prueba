package org.example;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.http.ContentType;
import io.javalin.http.Context;

import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        List<String> sitiosPermitidos = Arrays.asList("/HTML/Login.html","/HTML/index.html",
                "/css/Logstyle.css","/images/1.png","/HTML/formulario,html","/auntenticar");

        //
        var app = Javalin.create(javalinConfig -> {
                    //configurando los archivos estaticos
                    javalinConfig.staticFiles.add(staticFileConfig -> {
                        staticFileConfig.directory="/publico";
                        staticFileConfig.hostedPath="/";
                        staticFileConfig.location= Location.CLASSPATH;
                    });
                })
                //arracando el servidor
                .start(9090);

        /**
         * Filtro para validar la autenticacion
         */
        app.before(ctx -> {
            System.out.println("Configuracion: "+Configuracion.USUARIO.key);
            boolean sitioPermitido = sitiosPermitidos.stream().noneMatch(s -> {
                System.out.printf("Item[%s] = %s, validando: %b \n", s, ctx.path(), s.equalsIgnoreCase(ctx.path()));
                return s.equalsIgnoreCase(ctx.path());
            });

            //validando
            if(sitioPermitido) {
                Usuario usuario = ctx.sessionAttribute(Configuracion.USUARIO.key);
                if (usuario == null) {
                    ctx.redirect("/HTML/Login.html");
                }
            }

            //Dejando continuar con el flujo.
        });

        /**
         * End point dashboard.
         */
        app.get("/", ctx -> {
            ctx.contentType(ContentType.TEXT_HTML);
            ctx.result("<h1>Asignaci&oacute;n Aula </h1>");
        });

        /**
         * End point para auntenticar y redireccionara la barra ("/")
         */
        app.post("/auntenticar", ctx -> {
            //leyendo la información de
            Usuario usuario = new Usuario(ctx.formParam("usuario"), ctx.formParam("password"));
            ctx.sessionAttribute(Configuracion.USUARIO.key, usuario);
            ctx.redirect("/");
        });

    }

    /**
     * Uso de record, la forma de construir un objeto DTO
     * https://www.baeldung.com/java-record-keyword
     * @param usuario
     * @param password
     */
    public record Usuario(String usuario, String password){

    }

    /**
     * Uso de enum para los key en los sesiones.
     */
    public enum Configuracion{

        USUARIO("USUARIO");

        private String key;

        Configuracion(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }

}



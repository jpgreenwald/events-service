package com.swsandbox;

/**
 * User: jgreenwald
 * Date: 7/13/13
 * Time: 2:40 PM
 */
public class Help
{
    public static void main(String[] args)
    {
        System.out.printf("Simple Event Service:\n" +
                "\n" +
                "Available Servers:\n" +
                "\n" +
                "HealthServer - Simple HTTP REST resource with a quick reply\n" +
                "CaptureServer - REST interface that takes a JSON payload and sends it through a 0mq socket\n" +
                "WorkerServer - Binds to a 0mq socket and takes incoming JSON data and persists it to Cassandra DB\n" +
                "ZmqEchoServer - Binds to a 0mq socket and discards message\n" +
                "Health - This output\n" +
                "\n" +
                "Usage: \n" +
                "\n" +
                "java -cp capture-<version>.jar com.swsandbox.<server_name> [property_file_path]\n" +
                "\n" +
                "Properties:\n" +
                "\n" +
                "\t#numnber of threads for 0mq (1 per cpu core)\n" +
                "\tnum_of_context_threads=4\n" +
                "\t\n" +
                "\t#capture settings\n" +
                "\t#host for socket\n" +
                "\tcapture_socket_host=tcp://localhost:5100\n" +
                "\t\n" +
                "\t#number of sockets to place in pool\n" +
                "\tnum_of_push_sockets=4\n" +
                "\t\n" +
                "\t#port for HTTP REST api\n" +
                "\thttp_port=5050\n" +
                "\t\n" +
                "\t#timeout for http requests (in ms)\n" +
                "\thttp_idle_timeout=3000\n" +
                "\t\n" +
                "\t#worker\n" +
                "\tworker_socket_host=tcp://*:5100\n" +
                "\t\n" +
                "\t#cassandra db cluster host\n" +
                "\tcassandra_cluster_host=http://localhost\n" +
                "\n" +
                "\t#echo\n" +
                "\techo_count=100000");

    }
}

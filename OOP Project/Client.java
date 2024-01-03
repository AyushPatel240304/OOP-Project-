import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;

    public Client(Socket socket, String username)
    {
        try{
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.username = username;


        }
        catch (IOException e)
        {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void sendMessage()
    {
        try{
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();


            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected())
            {
                String message = scanner.nextLine();
                bufferedWriter.write(message);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }

        }
        catch (IOException e)
        {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void listenForMessage()
    {
        new Thread(new Runnable() {
            @Override
            public void run()
            {
                while (socket.isConnected())
                {
                    try
                    {
                        String message = bufferedReader.readLine();
                        System.out.println(message);
                    }
                    catch (IOException e)
                    {
                        closeEverything(socket, bufferedReader, bufferedWriter);
                    }
                }
            }
        }).start();
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter)
    {
        try{
            if(socket!=null)
            {
                socket.close();
            }
            if(bufferedReader != null)
            {
                bufferedReader.close();
            }
            if (bufferedWriter != null)
            {
                bufferedWriter.close();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("> Enter Your Username: ");
        String username = scanner.nextLine();

        Socket socket = new Socket("192.168.148.180", 9090);
        Client client = new Client(socket, username);
        client.listenForMessage();
        client.sendMessage();
    }
}

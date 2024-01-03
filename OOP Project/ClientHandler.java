import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements  Runnable{

    private static ArrayList<ClientHandler> clientHandlers= new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;

    public ClientHandler(Socket socket)
    {
        try{
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            this.username = bufferedReader.readLine();
            clientHandlers.add(this);
            broadCastMessage("Server |> "+username + "Has Just Connected !!");
        }
        catch (IOException e)
        {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    @Override
    public void run() {
        String messageFromClient;

        while (socket.isConnected())
        {
            try {
                messageFromClient = bufferedReader.readLine();
                broadCastMessage(username + " |> " + messageFromClient);
            }
            catch (IOException e)
            {
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    public void broadCastMessage(String message)
    {
        for(ClientHandler currClientHandler: clientHandlers)
        {
            try {
                if (!currClientHandler.equals(this))
                {
                    currClientHandler.bufferedWriter.write(message);
                    currClientHandler.bufferedWriter.newLine();
                    currClientHandler.bufferedWriter.flush();
                }
            }
            catch (IOException e)
            {
                closeEverything(socket, bufferedReader, bufferedWriter);

                break;
            }
        }
    }

    public void removeClientHandler()
    {
        clientHandlers.remove(this);
        broadCastMessage("Server |> " + username + " Has Disconnected");
    }

    private void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter)
    {
        removeClientHandler();
        try{
            if(bufferedReader != null)
            {
                bufferedReader.close();
            }

            if(bufferedWriter != null)
            {
                bufferedWriter.close();
            }

            if (socket != null)
            {
                socket.close();
            }
            System.out.println("Client Has Disconnected");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}

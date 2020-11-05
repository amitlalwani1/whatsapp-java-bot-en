<article class="container is-relative d-flex align-items-start" itemprop="articleBody">

<div class="content-radius">

In this article, we will look at how to write a simple WhatsApp bot in Java using our WhatsApp API.

<div class="nav-content mb-35">

<nav>

*   [Preparation](#preparation)
*   [A few words about why you need a webhook](#webhook)
*   [How to set up your own server in Java using the Spring Boot Framework](#javaspringboot)
*   [How to implement the logic of the controller](#logic)
*   [The ApiWA Class](#apiwa)
*   [How to implement the method of sending files](#sendfile)
*   [Java Whatsapp bot ready](#final)

</nav>

</div>

In the example below, the bot will receive commands in the form of regular WhatsApp messages and reply to them. Don’t forget to download the ready-to-use bot from our repository and use it in your work!

### What Functions Does the Bot Have?

1.  When you send the _chatid_ command, the bot replies with a message with the current chat ID;
2.  When you send the _file [format]_ command, the bot sends files (pdf, jpg, doc, mp3) that you have previously prepared;
3.  When you send the _ogg_ command, the bot sends a file with the .ogg extension (a voice message);
4.  When you send the _geo_ command, the bot sends GPS location;
5.  When you send the _group_ command, the bot creates a group chat for itself and the user;
6.  When you send any non-command messages, the bot replies with a welcome message describing all commands (as a sort of menu);

<div class="anchor-box" id="preparation">

## Preparation

<div class="d-flex flex-column flex-lg-row align-items-start mb-25">

<div class="pr-30">[![Authorization of Whatsapp via QR code](/img/whatsapp_auth_en.gif)](/img/whatsapp_auth_en.gif) </div>

<div class="">

The first thing you should do is to connect WhatsApp to our script so that you could check the bot’s work while you are writing the code. To do this, go to your [user account](https://app.chat-api.com) and get the QR code. Then open WhatsApp on your mobile phone and go to _Settings_ -> _WhatsApp Web_ -> _Scan the QR code_.

<div>[<span>Get access to WhatsApp API</span>](https://app.chat-api.com)</div>

</div>

</div>

<div class="is-relative">

<div class="note is-absolute">

<div class="note__title">Note!</div>

<div class="note__text">For the bot to work, your phone must be connected to the internet and must not be used for Whatsapp Web.</div>

</div>

</div>

In order to react to commands, our bot must be able to receive and process incoming data. This will require setting up your own webhook. Specify its address in your user account.

</div>

<div class="anchor-box" id="webhook">

### A Few Words About Why You Need a Webhook

A webhook helps prevent pauses in answering incoming messages. Without it, our bot would have to constantly, at regular intervals, send requests for incoming data to the server. That would slow the response time and increase the server load.

But if you specify the webhook’s address, you are spared this problem. The servers will send notifications about incoming changes as soon as they appear. The webhook, in its turn, will receive and process them correctly, thus implementing the bot’s logic. You can specify either the domain name or the IP address.

[![Put webhook in your personal cabinet](/img/java/webhook-min.jpg) <span class="image-caption">Set up webhook in your personal profile</span>](/img/java/webhook-min.jpg) </div>

<div class="anchor-box" id="javaspringboot">

## How to Set Up Your Own Server in Java Using the Spring Boot Framework

To simplify the process of making the template of our spring boot app, use [spring initializr](https://start.spring.io/): specify the necessary settings, pick the required Java version, and click on _Generate_

<div class="mb-30">[![Setting up your own server in Java using the Spring Boot framework for the bot](/img/java/java1-min.png) <span class="image-caption">Setting up your own server in Java using the Spring Boot framework for the bot</span>](/img/java/java1-min.png) </div>

The next step is to download the generated project and open it in your IDE. In our example, we will use [Intellij IDEA](https://www.jetbrains.com/idea/). Create a _"controller"_ folder where you will keep your controller (it will process the data incoming to your server) and the classes necessary for the bot’s work.

<div class="mb-30">[![](/img/java/java2-min.png)](/img/java/java2-min.png) </div>

Chat API sends JSON data that we will need to parse and process.

To see what particular data is sent to JSON, open the [тестирование](https://app.chat-api.com/testing) page in your user account, and go to the _Webhook Simulator_ tab.

<div class="mb-30">[![Simulation of the webhook in a personal chat-api cabinet](/img/java/java3-min.png) <span class="image-caption">Simulation of the webhook in a personal chat-api cabinet</span>](/img/java/java3-min.png) </div>

**JSON body** is the data (the structure of the data) that will be sent to our server. To comfortably work with it in Java, we will deserialize it into an object and treat the data as object properties.

For your convenience, use [the service](http://www.jsonschema2pojo.org/) that allows for the automatic generation of JSON string to Java class. Copy JSON body, click on _Preview_ and paste the automatically generated Java classes to our project. For this purpose, create the _jsonserializables.java_ file and paste the code to the file.

<div class="mb-30">[![Json to Java generation](/img/java/java4-min.png) <span class="image-caption">Json to Java generation</span>](/img/java/java4-min.png) </div>

<div class="mb-30">[![](/img/java/java5-min.png)](/img/java/java5-min.png) </div>

After the class describing JSON body has been created, it’s time to start implementing the controller itself. The latter will process incoming data. Create a _MessageController_ file and define the MessageController class in it.

<div class="mb-30">[![](/img/java/java6-min.png)](/img/java/java6-min.png) </div>

    @RestController
    @RequestMapping("webhook")
        public class MessageController {
            @PostMapping
            public String AnswerWebhook(@RequestBody RequestWebhook hook) throws IOException {
                return  "ok";
            }

The **@RequestMapping("webhook")** annotation designates the address where our controller will process data. Example: "localhost:8080/webhook"

The **@PostMapping** annotation means that the AnswerWebhook function will process POST requests.

At the same time, the function’s parameter **RequestWebhook** is our deserialized JSON class whose deserialization is performed by Spring Boot. So, we get a ready-to-use object and can work with it within the function itself. Also within the function, we will describe the bot’s logic.

</div>

<div class="anchor-box" id="logic">

## How to Implement the Logic of the Controller

    @RestController
        @RequestMapping("webhook")
        public class MessageController {
            @PostMapping
            public String AnswerWebhook(@RequestBody RequestWebhook hook) throws IOException {
                for (var message : hook.getMessages()) {
                    if (message.getFromMe())
                        continue;
                    String option = message.getBody().split(" ")[0].toLowerCase();
                    switch (option)
                    {
                        case "chatid":
                            ApiWA.sendChatId(message.getChatId());
                            break;
                        case "file":
                            var texts = message.getBody().split(" ");
                            if (texts.length > 1)
                                ApiWA.sendFile(message.getChatId(), texts[1]);
                            break;
                        case "ogg":
                            ApiWA.sendOgg(message.getChatId());
                            break;
                        case "geo":
                            ApiWA.sendGeo(message.getChatId());
                            break;
                        case "group":
                            ApiWA.createGroup(message.getAuthor());
                            break;
                        default:
                            ApiWA.sendDefault(message.getChatId());
                            break;
                    }
                }
                return  "ok";
            }

Within the cycle, go over all the incoming messages and process them in switch. It is necessary to check the bot’s work at the beginning of the cycle to make sure it will not loop into a recursive call. In case you get a message from yourself, just skip (don’t process) it.

Next, assign the received command to the **option** variable. To do this, use the _split_ method to split the message body and lower-case the command text so that your bot could respond to commands irrespective of the case. In _switch_, depending on the incoming command, call the required method from the ApiWA class (see below).

</div>

<div class="anchor-box" id="apiwa">

## The ApiWA Class

This class will implement static [API](https://app.chat-api.com/docs) methods. Within the class, we will describe the variables that will keep our token — so that we could successfully use the API when sending these data. You can find your token in your [user account](https://app.chat-api.com/dashboard).

    public class ApiWA {
            private static String APIURL = "https://eu115.chat-api.com/instance123456/";
            private static String TOKEN = "1hi0xw1fzaen1234";
        }

Next, we need to implement a method that will send a POST request.

     public static CompletableFuture<Void> postJSON(URI uri,
                                                Map<String,String> map)
                throws IOException
        {
            ObjectMapper objectMapper = new ObjectMapper();
            String requestBody = objectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(map);

            HttpRequest request = HttpRequest.newBuilder(uri)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            return HttpClient.newHttpClient()
                    .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::statusCode)
                    .thenAccept(System.out::println);
        }

This method’s parameters include an URI link (where you will have to send a POST request) and a dictionary that you will need to serialize into a JSON string and send to the server.

To implement each of these methods, you will have to see detailed [documentation](https://app.chat-api.com/docs) and repeat requests.

Here is what the **chatid** sending method looks like:

        public static void sendChatId(String chat_id) throws IOException {
            URI uri = URI.create(APIURL + "sendMessage?token=" + TOKEN);
            Map<String, String> map = new HashMap<String, String>();
            map.put("body", "Your ID: " + chat_id);
            map.put("phone", chat_id);
            ApiWA.postJSON(uri, map);
        }

<div class="is-relative">

Now you need to form a URI where you will send requests and a dictionary with parameters. After that, use the method of sending POST requests that we have implemented above.

We described the implementation of sending a file via Whatsapp chatbot in the next chapter, and the rest of the functionality, such as sending geolocation, creating a group and other commands, you can see in the source code of our bot.

<div class="note is-absolute">

<div class="note__title">Other Commands</div>

<div class="note__text">Other methods are implemented in a similar way. As for the source code, you can view and download it at [Github](https://github.com/chatapi/whatsapp-java-bot-en "github example whatsapp bot java").</div>

</div>

</div>

</div>

<div class="anchor-box" id="sendfile">

### How to Implement the Method of Sending Files

     public static void sendFile(String chat_id, String file_format) throws IOException {
            Map<String, String> formats= new HashMap<String, String>();
            formats.put("doc", Base64Help.getDOC());
            formats.put("jpeg", Base64Help.getJPEG());
            formats.put("pdf", Base64Help.getPDFtring());
            formats.put("mp3", Base64Help.getMP3String());

            if (formats.containsKey(file_format))
            {
                Map<String, String> map = new HashMap<String, String>();
                map.put("phone", chat_id);
                map.put("body", formats.get(file_format));
                map.put("filename", "ThisIsFile");
                map.put("caption", "ThisIsCaption");
                URI uri = URI.create(APIURL + "sendFile?token=" + TOKEN);
                ApiWA.postJSON(uri, map);
            }
            else
            {
                Map<String, String> map = new HashMap<String, String>();
                map.put("phone", chat_id);
                map.put("body", "File not found");
                URI uri = URI.create(APIURL + "sendMessage?token=" + TOKEN);
                ApiWA.postJSON(uri, map);
            }
        }

This method contains a dictionary where the keys are file formats while the values are strings in the Base64 format. To generate a string, you can use a dedicated [service](https://app.chat-api.com/base64) on our website. If a file format you need is not in the dictionary, you will get a notification saying that the file format is not supported.

We have described the Base64Help class and methods that allow you to get a string with a file of the required format. The string itself is stored in TXT files on the server and when we use the code, we just read it from the file. This is vital because, in Java, you cannot store strings that long right in the code. You can generate a Base64 string either automatically or through special services.

    public class Base64Help {
            static public String getPDFString() throws IOException {
                return new String(Files.readAllBytes(Paths.get("src/main/resources/pdf.txt")));
            }

            static  public String getMP3String() throws IOException {
                return new String(Files.readAllBytes(Paths.get("src/main/resources/mp3.txt")));
            }

            static public String getJPEG() throws IOException {
                return new String(Files.readAllBytes(Paths.get("src/main/resources/jpeg.txt")));
            }

            static public String getDOC() throws IOException {
                return new String(Files.readAllBytes(Paths.get("src/main/resources/doc.txt")));
            }

        }

Summing up, we have described the work of a simple WhatsApp bot and uploaded its fully functioning source code to Github.

All you will need to do is to insert your token (specified in your user account) and your instance number into the code.




</article>
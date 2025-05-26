using Google.Apis.Auth.OAuth2;
using Google.Apis.Services;
using Google.Apis.FirebaseCloudMessaging.v1;
using Google.Apis.FirebaseCloudMessaging.v1.Data;

namespace NotiAdmin

{
    public partial class Form1 : Form
    {

        private string projectId = "fcmproyectowpf"; // Tu ID de proyecto
        private string serviceAccountPath = "serviceAccountKey.json"; // Asegúrate de que esté en el directorio correcto

        public Form1()
        {
            InitializeComponent();
        }

        private async void button1_Click(object sender, EventArgs e)
        {
            string token = txtToken.Text.Trim();

            if (string.IsNullOrWhiteSpace(token))
            {
                MessageBox.Show("Por favor, ingresa un token válido.");
                return;
            }

            try
            {
                //Obtener las credenciales del archivo de propiedades del sistema.
                var credential = GoogleCredential
                    .FromFile(serviceAccountPath)
                    .CreateScoped("https://www.googleapis.com/auth/firebase.messaging");

                //Creamos el servicio de FCM Objeto service interactua con la API Rest
                var service = new FirebaseCloudMessagingService(new BaseClientService.Initializer
                {
                    HttpClientInitializer = credential,
                    ApplicationName = "AdminNotificaciones",
                });

                //Preparacion del mensaje
                var message = new Google.Apis.FirebaseCloudMessaging.v1.Data.Message
                {
                    Token = token,
                    Notification = new Notification
                    {
                        Title = txtTitulo.Text,
                        Body = txtMensaje.Text
                    }
                };
                //Enviar la notificación.
                var request = new SendMessageRequest { Message = message };

                //Se crea la solicitud y se envía a Firebase usando el endpoint:
                var result = await service.Projects.Messages.Send(request, $"projects/{projectId}").ExecuteAsync();

                //MessageBox.Show("¡Notificación enviada! ID: " + result.Name);
                MessageBox.Show("¡Notificación enviada!");

            }
            catch (Exception ex)
            {
                MessageBox.Show("¡Error al enviar la notificación!");
                //MessageBox.Show("Error al enviar la notificación:\n" + ex.Message);
            }

        }

        private void label2_Click(object sender, EventArgs e)
        {

        }
    }
}

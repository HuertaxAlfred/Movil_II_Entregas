namespace NotiAdmin
{
    partial class Form1
    {
        /// <summary>
        ///  Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        ///  Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        ///  Required method for Designer support - do not modify
        ///  the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            bntEnviar = new Button();
            txtMensaje = new TextBox();
            txtToken = new TextBox();
            txtTitulo = new TextBox();
            label1 = new Label();
            label2 = new Label();
            label3 = new Label();
            SuspendLayout();
            // 
            // bntEnviar
            // 
            bntEnviar.BackColor = Color.PaleTurquoise;
            bntEnviar.Font = new Font("Segoe UI", 12F, FontStyle.Regular, GraphicsUnit.Point, 0);
            bntEnviar.ForeColor = SystemColors.ControlText;
            bntEnviar.ImageAlign = ContentAlignment.MiddleRight;
            bntEnviar.Location = new Point(104, 365);
            bntEnviar.Name = "bntEnviar";
            bntEnviar.Size = new Size(165, 59);
            bntEnviar.TabIndex = 0;
            bntEnviar.Text = "Enviar notificación";
            bntEnviar.UseVisualStyleBackColor = false;
            bntEnviar.Click += button1_Click;
            // 
            // txtMensaje
            // 
            txtMensaje.Location = new Point(64, 206);
            txtMensaje.Multiline = true;
            txtMensaje.Name = "txtMensaje";
            txtMensaje.Size = new Size(249, 118);
            txtMensaje.TabIndex = 1;
            // 
            // txtToken
            // 
            txtToken.Location = new Point(64, 53);
            txtToken.Name = "txtToken";
            txtToken.Size = new Size(249, 23);
            txtToken.TabIndex = 2;
            // 
            // txtTitulo
            // 
            txtTitulo.Location = new Point(64, 121);
            txtTitulo.Name = "txtTitulo";
            txtTitulo.Size = new Size(249, 23);
            txtTitulo.TabIndex = 3;
            // 
            // label1
            // 
            label1.AutoSize = true;
            label1.Location = new Point(124, 188);
            label1.Name = "label1";
            label1.Size = new Size(145, 15);
            label1.TabIndex = 4;
            label1.Text = "Mensaje de la notificación";
            // 
            // label2
            // 
            label2.AutoSize = true;
            label2.Location = new Point(115, 35);
            label2.Name = "label2";
            label2.Size = new Size(164, 15);
            label2.TabIndex = 5;
            label2.Text = "Digita el Token del dispositivo";
            label2.Click += label2_Click;
            // 
            // label3
            // 
            label3.AutoSize = true;
            label3.Location = new Point(124, 103);
            label3.Name = "label3";
            label3.Size = new Size(132, 15);
            label3.TabIndex = 6;
            label3.Text = "Titulo de la notificación";
            // 
            // Form1
            // 
            AutoScaleDimensions = new SizeF(7F, 15F);
            AutoScaleMode = AutoScaleMode.Font;
            BackgroundImageLayout = ImageLayout.None;
            ClientSize = new Size(376, 455);
            Controls.Add(label3);
            Controls.Add(label2);
            Controls.Add(label1);
            Controls.Add(txtTitulo);
            Controls.Add(txtToken);
            Controls.Add(txtMensaje);
            Controls.Add(bntEnviar);
            Name = "Form1";
            Text = "Panel de envio de notificaciones";
            ResumeLayout(false);
            PerformLayout();
        }

        #endregion

        private Button bntEnviar;
        private TextBox txtMensaje;
        private TextBox txtToken;
        private TextBox txtTitulo;
        private Label label1;
        private Label label2;
        private Label label3;
    }
}

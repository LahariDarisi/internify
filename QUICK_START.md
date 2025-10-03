# ⚡ Quick Start: Deploy Internify to Render in 5 Minutes

## 🚀 Super Fast Deployment Steps

### **1️⃣ Push to GitHub** (2 min)

```bash
git add .
git commit -m "Ready for Render deployment"
git push origin main
```

### **2️⃣ Deploy on Render** (2 min)

1. Go to [render.com](https://render.com) and sign up/login
2. Click **"New +"** → **"Blueprint"**
3. Connect your GitHub repo
4. Select your `internify` repository
5. Click **"Apply"**

✅ Render will automatically:
- Create MySQL database
- Deploy your Spring Boot app
- Set up health checks

### **3️⃣ Set Environment Variables** (1 min)

In Render dashboard → Your Web Service → Environment:

**Required variables:**
```
MAIL_PASSWORD=your-gmail-app-password
```

**Get Gmail App Password:**
- Go to [myaccount.google.com](https://myaccount.google.com)
- Security → App Passwords → Generate
- Copy and paste into Render

### **4️⃣ Test Your API** 

```bash
# Replace with your Render URL
curl https://your-app.onrender.com/actuator/health
```

**Expected response:**
```json
{"status":"UP"}
```

---

## 🎯 Your Deployed URLs

- **API Base**: `https://internify-backend.onrender.com`
- **Health Check**: `https://internify-backend.onrender.com/actuator/health`
- **Login**: `POST https://internify-backend.onrender.com/api/auth/login`
- **Register Student**: `POST https://internify-backend.onrender.com/api/auth/register/student`

---

## 📝 API Testing Examples

### Register Student
```bash
curl -X POST https://your-app.onrender.com/api/auth/register/student \
  -H "Content-Type: application/json" \
  -d '{
    "email": "student@test.com",
    "password": "password123",
    "name": "John Doe"
  }'
```

### Login
```bash
curl -X POST https://your-app.onrender.com/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "student@test.com",
    "password": "password123"
  }'
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1Ni...",
  "userId": "1",
  "userType": "STUDENT"
}
```

### Use Token for Protected Endpoints
```bash
curl -X GET https://your-app.onrender.com/api/students/me \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

---

## ⚠️ Important Notes

### Free Tier Limitations:
- 🌙 **Sleeps after 15 min idle** → First request takes 50+ seconds
- 💾 **Ephemeral storage** → Uploaded files deleted on restart
- 📊 **1GB database** → Enough for personal projects

### Keep Your App Awake (Optional):
Use a service like [UptimeRobot](https://uptimerobot.com/) to ping your health check endpoint every 5 minutes.

---

## 🐛 Troubleshooting

### App won't start?
1. Check **Logs** in Render dashboard
2. Verify all environment variables are set
3. Ensure DATABASE_URL is correct

### Database connection failed?
- Wait 2-3 minutes for database to fully initialize
- Check database is in same region as web service
- Verify DB credentials in environment variables

### Build fails?
- Check Java version (should be 21)
- Ensure `mvnw` has execute permissions
- Look at build logs for specific errors

---

## 🎨 Next Steps

1. **Deploy ML API**: If you have the Python ML service, deploy it separately
2. **Update ML_API_URL**: Point to your deployed ML API
3. **Deploy Frontend**: Use Vercel, Netlify, or Render Static Sites
4. **Custom Domain**: Add in Render settings (free SSL included)
5. **Monitor**: Set up logging and error tracking

---

## 📞 Support

- **Render Docs**: https://render.com/docs
- **Spring Boot Guide**: https://render.com/docs/deploy-spring-boot
- **Render Community**: https://community.render.com

---

**That's it! Your app is live! 🎉**


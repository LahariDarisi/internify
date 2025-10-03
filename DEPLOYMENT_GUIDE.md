# 🚀 Deployment Guide: Internify on Render

## Prerequisites
- GitHub account
- Render account (free tier available at [render.com](https://render.com))
- Your code pushed to GitHub
- Gmail App Password for email functionality

---

## 📋 Step-by-Step Deployment

### **Step 1: Push Your Code to GitHub**

```bash
# Initialize git (if not already done)
git init

# Add all files
git add .

# Commit
git commit -m "Initial commit - ready for Render deployment"

# Create a new repository on GitHub, then:
git remote add origin https://github.com/YOUR_USERNAME/internify.git
git branch -M main
git push -u origin main
```

> ⚠️ **IMPORTANT**: Create a `.gitignore` file to exclude sensitive files!

---

### **Step 2: Create `.gitignore` File**

Create this file in your project root:

```
target/
!.mvn/wrapper/maven-wrapper.jar
!**/src/main/**/target/
!**/src/test/**/target/

### STS ###
.apt_generated
.classpath
.factorypath
.project
.settings
.springBeans
.sts4-cache

### IntelliJ IDEA ###
.idea
*.iws
*.iml
*.ipr

### NetBeans ###
/nbproject/private/
/nbbuild/
/dist/
/nbdist/
/.nb-gradle/
build/
!**/src/main/**/build/
!**/src/test/**/build/

### VS Code ###
.vscode/

### Uploads ###
src/main/resources/uploads/
```

---

### **Step 3: Get Gmail App Password**

1. Go to [Google Account Settings](https://myaccount.google.com/)
2. Enable **2-Step Verification** (required for app passwords)
3. Go to **Security** → **App Passwords**
4. Generate a new app password for "Mail"
5. Save this password (you'll need it in Step 5)

---

### **Step 4: Deploy to Render**

#### **Option A: Using render.yaml (Recommended) ✅**

1. Go to [Render Dashboard](https://dashboard.render.com/)
2. Click **"New +"** → **"Blueprint"**
3. Connect your GitHub repository
4. Render will detect `render.yaml` and set up:
   - Web Service (Backend API)
   - MySQL Database (Free tier)

#### **Option B: Manual Setup**

1. **Create Database First:**
   - Click **"New +"** → **"MySQL"**
   - Name: `internify-db`
   - Database: `internify_db`
   - Plan: **Free**
   - Click **"Create Database"**

2. **Create Web Service:**
   - Click **"New +"** → **"Web Service"**
   - Connect your GitHub repo
   - Name: `internify-backend`
   - Runtime: **Docker**
   - Plan: **Free**
   - Click **"Create Web Service"**

---

### **Step 5: Configure Environment Variables**

In your Render Web Service dashboard, go to **"Environment"** and add:

```
SPRING_PROFILES_ACTIVE=prod
PORT=8080
JWT_SECRET=your-super-secret-jwt-key-at-least-32-chars-long-please
MAIL_USERNAME=internifyplatform@gmail.com
MAIL_PASSWORD=your-gmail-app-password-here
ML_API_URL=http://localhost:5000
```

For database variables (if using manual setup):
```
DATABASE_URL=jdbc:mysql://[INTERNAL_HOST]:3306/internify_db?useSSL=false&serverTimezone=UTC
DB_USERNAME=[from Render DB dashboard]
DB_PASSWORD=[from Render DB dashboard]
```

> 💡 **Tip**: Get DB credentials from your Render MySQL dashboard → "Connection Details"

---

### **Step 6: Update CORS Configuration**

Once deployed, update these files with your Render URL:

**SecurityConfig.java:**
```java
config.setAllowedOrigins(Arrays.asList(
    "http://localhost:3000", 
    "http://localhost:4200",
    "https://your-frontend-app.onrender.com"  // Add your frontend URL
));
```

**WebConfig.java:**
```java
.allowedOrigins(
    "http://localhost:3000", 
    "http://localhost:5173",
    "https://your-frontend-app.onrender.com"  // Add your frontend URL
)
```

Redeploy after making these changes.

---

### **Step 7: Deploy ML API (Optional)**

If you have a separate ML API:

1. Create another Web Service on Render
2. Use Python runtime
3. Deploy your Flask/FastAPI ML service
4. Update `ML_API_URL` environment variable with the deployed URL

---

## 🎯 Testing Your Deployment

### **1. Health Check**
```bash
curl https://your-app-name.onrender.com/actuator/health
```

Expected response:
```json
{"status":"UP"}
```

### **2. Register a Student**
```bash
curl -X POST https://your-app-name.onrender.com/api/auth/register/student \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123",
    "name": "Test Student"
  }'
```

### **3. Login**
```bash
curl -X POST https://your-app-name.onrender.com/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'
```

---

## ⚙️ Render Free Tier Limitations

- **Web Services**: Spin down after 15 minutes of inactivity
- **First request after sleep**: Takes 50+ seconds to wake up
- **Database**: 1GB storage limit
- **Build time**: 15 minutes max
- **Monthly hours**: 750 hours free (one instance 24/7)

---

## 🐛 Common Issues & Solutions

### **Issue 1: Build Fails**
**Solution**: Check build logs. Often due to:
- Missing Maven wrapper files
- Wrong Java version
- Dependency download issues

### **Issue 2: Database Connection Failed**
**Solution**: 
- Verify DATABASE_URL format
- Check DB credentials match Render dashboard
- Ensure database is in same region as web service

### **Issue 3: Application Won't Start**
**Solution**:
- Check environment variables are set correctly
- Look at runtime logs in Render dashboard
- Verify JWT_SECRET is at least 32 characters

### **Issue 4: File Upload Fails**
**Solution**: 
- Render's free tier uses ephemeral storage
- Files uploaded will be lost on restart
- Consider using AWS S3 or Cloudinary for production

### **Issue 5: CORS Errors**
**Solution**:
- Update `SecurityConfig.java` and `WebConfig.java` with your frontend URL
- Redeploy the service

---

## 📊 Monitoring Your App

1. **Logs**: Render Dashboard → Your Service → "Logs"
2. **Metrics**: View CPU, Memory, Request counts
3. **Health Checks**: Auto-configured via `/actuator/health`

---

## 🔧 Updating Your App

```bash
# Make changes to your code
git add .
git commit -m "Update feature X"
git push origin main
```

Render will **automatically redeploy** when you push to GitHub! 🎉

---

## 🎨 Next Steps

1. **Deploy Frontend**: Use Render Static Sites or Vercel
2. **Custom Domain**: Add your domain in Render settings
3. **SSL**: Automatically provided by Render (free)
4. **Database Backups**: Set up regular backups
5. **Monitoring**: Add logging with Sentry or LogRocket

---

## 📱 Your App URLs

After deployment, you'll have:
- **API**: `https://internify-backend.onrender.com`
- **Database**: Internal Render URL (private)
- **Health Check**: `https://internify-backend.onrender.com/actuator/health`

---

## 🆘 Need Help?

- [Render Docs](https://render.com/docs)
- [Spring Boot on Render Guide](https://render.com/docs/deploy-spring-boot)
- Check Render community forum

---

**Happy Deploying! 🚀**


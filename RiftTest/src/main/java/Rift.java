import static org.lwjgl.ovr.OVR.*;
import static org.lwjgl.ovr.OVRGL.*;
import static org.lwjgl.ovr.OVRUtil.*;
import static org.lwjgl.ovr.OVRErrorCode.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.opengl.ARBFramebufferObject.*;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.BufferUtils;
import org.lwjgl.ovr.OVRDetectResult;
import org.lwjgl.ovr.OVREyeRenderDesc;
import org.lwjgl.ovr.OVRFovPort;
import org.lwjgl.ovr.OVRGL;
import org.lwjgl.ovr.OVRGLTexture;
import org.lwjgl.ovr.OVRGraphicsLuid;
import org.lwjgl.ovr.OVRHmdDesc;
import org.lwjgl.ovr.OVRInitParams;
import org.lwjgl.ovr.OVRLayerEyeFov;
import org.lwjgl.ovr.OVRLogCallback;
import org.lwjgl.ovr.OVRMatrix4f;
import org.lwjgl.ovr.OVRPosef;
import org.lwjgl.ovr.OVRRecti;
import org.lwjgl.ovr.OVRSizei;
import org.lwjgl.ovr.OVRSwapTextureSet;
import org.lwjgl.ovr.OVRTexture;
import org.lwjgl.ovr.OVRTrackingState;
import org.lwjgl.ovr.OVRUtil;
import org.lwjgl.ovr.OVRVector3f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opengl.GL;

public class Rift {
	private long hmd;
	private OVRHmdDesc hmdDesc;
	private OVRFovPort[] fovPorts = new OVRFovPort[2];
	private OVRPosef[] eyePoses = new OVRPosef[2];
	private OVRMatrix4f[] projections = new OVRMatrix4f[2];
	private OVREyeRenderDesc[] eyeRenderDescs = new OVREyeRenderDesc[2];
	private OVRSwapTextureSet textureSet;
	private OVRGLTexture[] textures;
	private FrameBufferObject[] fbos;
	private OVRLayerEyeFov layer0;
	private PointerBuffer layers;
	private int resolutionWidth;
	private int resolutionHeight;
	private int textureWidth;
	private int textureHeight;
	private Scene scene;
	private int texturesPerEyeCount;
	private int currentTexturesPerEyeIndex = -1;
	private OVRTexture mirrorTexture;

	public Rift(Scene scene) {
		this.scene = scene;
		//check if oculus and services is available
		OVRDetectResult detect = OVRDetectResult.calloc();
		ovr_Detect(0, detect);
		if (!detect.IsOculusHMDConnected() || !detect.IsOculusServiceRunning()) {
			throw new IllegalStateException("Oculus not detected or service not running.");
		} else {
			System.out.println("Oculus connected and service is running.");
		}
		detect.free();

		//init hmd
		OVRInitParams initParams = OVRInitParams.calloc();
		initParams.LogCallback(new OVRLogCallback() {
			@Override
			public void invoke(long userData, int level, long message) {
				//TODO maybe extend msg
				System.out.println(memDecodeASCII(message));
			}
		});
		if (ovr_Initialize(initParams) != ovrSuccess) {
			throw new RuntimeException("Couldn't initialize ovr.");
		}
		initParams.free();

		//create hmd
		PointerBuffer hmdPointer = memAllocPointer(1);
		OVRGraphicsLuid luid = OVRGraphicsLuid.calloc();
		if (ovr_Create(hmdPointer, luid) != ovrSuccess) {
			throw new RuntimeException("Couldn't create hmd.");
		}
		hmd = hmdPointer.get(0);
		memFree(hmdPointer);
		luid.free();

		//get hmd desc
		hmdDesc = OVRHmdDesc.malloc();
		ovr_GetHmdDesc(hmd, hmdDesc);
		System.out.println("OVR: " + hmdDesc.ManufacturerString() + " - " + hmdDesc.ProductNameString() + "\n");
		if (hmdDesc.Type() == ovrHmd_None) {
			throw new RuntimeException("Couldn't create correct hmd. Might be not correct initilialzed.");
		}

		resolutionWidth = hmdDesc.Resolution().w();
		resolutionHeight = hmdDesc.Resolution().h();

		//FOV, projections and renderDesc
		for (int eye = 0; eye < 2; eye++) {
			fovPorts[eye] = hmdDesc.DefaultEyeFov(eye);
			projections[eye] = OVRMatrix4f.malloc();
			ovrMatrix4f_Projection(fovPorts[eye], 0.5f, 500f, ovrProjection_RightHanded, projections[eye]);
//			ovrMatrix4f_Projection(fovPorts[eye], 0.5f, 500f, ovrProjection_None, projections[eye]);
			eyeRenderDescs[eye] = OVREyeRenderDesc.malloc();
			ovr_GetRenderDesc(hmd, eye, fovPorts[eye], eyeRenderDescs[eye]);

			System.out.println("eye " + eye + " = " + fovPorts[eye].UpTan() + ", " + fovPorts[eye].DownTan() + ", "
					+ fovPorts[eye].LeftTan() + ", " + fovPorts[eye].RightTan());
			System.out.println("ipd eye " + eye + " = " + eyeRenderDescs[eye].HmdToEyeViewOffset().x());
		}

		//recenter view
		ovr_RecenterPose(hmd);
	}

	public void init() {
		//FIXME is this needed here?
		GL.createCapabilities();

		float pixelsPerDisplayPixel = 1;
		OVRSizei leftTextureSize = OVRSizei.malloc();
		ovr_GetFovTextureSize(hmd, ovrEye_Left, fovPorts[ovrEye_Left], pixelsPerDisplayPixel, leftTextureSize);

		OVRSizei rightTextureSize = OVRSizei.malloc();
		ovr_GetFovTextureSize(hmd, ovrEye_Right, fovPorts[ovrEye_Right], pixelsPerDisplayPixel, rightTextureSize);

		textureWidth = (leftTextureSize.w() + rightTextureSize.w());
		textureHeight = Math.max(leftTextureSize.h(), rightTextureSize.h());
		System.out.println("Texture size: " + textureWidth + " x " + textureHeight);

		leftTextureSize.free();
		rightTextureSize.free();

		PointerBuffer textureSetPointerBuffer = BufferUtils.createPointerBuffer(1);
		if (ovr_CreateSwapTextureSetGL(hmd, GL_RGBA, textureWidth, textureHeight,
				textureSetPointerBuffer) != ovrSuccess) {
			throw new IllegalStateException("Couldn't create swap texture set.");
		}
		long hts = textureSetPointerBuffer.get(0);
		textureSet = OVRSwapTextureSet.create(hts);

		PointerBuffer colorTexturePointerBuffer = BufferUtils.createPointerBuffer(2);
		colorTexturePointerBuffer.put(0, textureSet.address());
		colorTexturePointerBuffer.put(1, textureSet.address());

		textures = new OVRGLTexture[2];
		fbos = new FrameBufferObject[2];
		//TODO CHECK CHANGELOG FOR Textures()
		long hTextures = textureSet.Textures().address();
		for (int eye = 0; eye < 2; eye++) {
			OVRGLTexture texture = OVRGLTexture.create(hTextures + (eye * OVRGLTexture.SIZEOF));
			textures[eye] = texture;
			OVRSizei size = texture.OGL().Header().TextureSize();
			fbos[eye] = new FrameBufferObject(size.w(), size.h(), texture.OGL().TexId());
		}

		OVRRecti[] viewports = new OVRRecti[2];
		for (int eye = 0; eye < 2; eye++) {
			viewports[eye] = OVRRecti.calloc();
			viewports[eye].Pos().x(0);
			viewports[eye].Pos().y(0);
			viewports[eye].Size().w(fbos[eye].getWidth());
			viewports[eye].Size().h(fbos[eye].getHeight());
		}

		layer0 = OVRLayerEyeFov.calloc();
		layer0.Header().Type(ovrLayerType_EyeFov);
		layer0.Header().Flags(ovrLayerFlag_TextureOriginAtBottomLeft);
		for (int eye = 0; eye < 2; eye++) {
			layer0.ColorTexture(colorTexturePointerBuffer);
			layer0.Viewport(eye, viewports[eye]);
			layer0.Fov(eye, fovPorts[eye]);
			viewports[eye].free();
		}
		
		layers = BufferUtils.createPointerBuffer(1);
		layers.put(0, layer0);
		
		texturesPerEyeCount = textureSet.TextureCount();
		
		scene.init();
	}
	
	public void render(){
		
		double timing = ovr_GetPredictedDisplayTime(hmd, 0);
		OVRTrackingState trackingState = OVRTrackingState.malloc();
		ovr_GetTrackingState(hmd, timing, true, trackingState);
		OVRPosef headPose = trackingState.HeadPose().ThePose();
		trackingState.free();
		
		OVRVector3f.Buffer hmdToEyeViewOffsets = OVRVector3f.calloc(2);
		hmdToEyeViewOffsets.put(0, eyeRenderDescs[ovrEye_Left].HmdToEyeViewOffset());
		hmdToEyeViewOffsets.put(1, eyeRenderDescs[ovrEye_Right].HmdToEyeViewOffset());
		
		OVRPosef.Buffer outEyePoses = OVRPosef.create(2);
		ovr_CalcEyePoses(headPose, hmdToEyeViewOffsets, outEyePoses);
		eyePoses[ovrEye_Left] = outEyePoses.get(0);
		eyePoses[ovrEye_Right] = outEyePoses.get(1);
		
		currentTexturesPerEyeIndex = (currentTexturesPerEyeIndex + 1) % texturesPerEyeCount;
		for(int eye = 0; eye < 2; eye++){
			OVRPosef eyePose = eyePoses[eye];
			layer0.RenderPose(eye, eyePose);
			textureSet.CurrentIndex(currentTexturesPerEyeIndex);
			fbos[currentTexturesPerEyeIndex].bind();
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			glViewport(0, 0, textureWidth, textureHeight);
			scene.setEye(eye);
			scene.render();
			
		}
		fbos[0].unbind();
		glBindTexture(GL_TEXTURE_2D, 0);
		
		int result = ovr_SubmitFrame(hmd, 0, null, layers);
		if(result == ovrSuccess_NotVisible){
			System.out.println("FRAME NOT VISIBLE");
		}
		if(result != ovrSuccess){
			System.err.println("FRAME SUBMIT FAILED!");
		}
	}

	public void destroy() {
		for (OVRMatrix4f projection : projections) {
			projection.free();
		}

		for (OVREyeRenderDesc eyeRenderDesc : eyeRenderDescs) {
			eyeRenderDesc.free();
		}

		scene.destroy();
		hmdDesc.free();
		ovr_DestroyMirrorTexture(hmd, mirrorTexture);
		ovr_DestroySwapTextureSet(hmd, textureSet);
		ovr_Destroy(hmd);
		ovr_Shutdown();
	}
	
	public int getMirrorFramebuffer(int width, int height){
		PointerBuffer outMirrorTexture = BufferUtils.createPointerBuffer(1);
		OVRGL.ovr_CreateMirrorTextureGL(hmd, GL_RGBA, width, height, outMirrorTexture);
		long address = outMirrorTexture.get();
		OVRGLTexture ovrTexture = OVRGLTexture.create(address);
		int texture = ovrTexture.OGL().TexId();
		
		mirrorTexture = ovrTexture.Texture();
		
		int framebuffer = glGenFramebuffers();
		glBindFramebuffer(GL_READ_FRAMEBUFFER, framebuffer);
		glFramebufferTexture2D(GL_READ_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture, 0);
		glFramebufferRenderbuffer(GL_READ_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, 0);
		glBindFramebuffer(GL_READ_FRAMEBUFFER, 0);
		
		return framebuffer;
	}

}

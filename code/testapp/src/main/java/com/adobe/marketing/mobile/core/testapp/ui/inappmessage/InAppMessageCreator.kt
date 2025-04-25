/*
  Copyright 2024 Adobe. All rights reserved.
  This file is licensed to you under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software distributed under
  the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR REPRESENTATIONS
  OF ANY KIND, either express or implied. See the License for the specific language
  governing permissions and limitations under the License.
 */
package com.adobe.marketing.mobile.core.testapp.ui.inappmessage

import com.adobe.marketing.mobile.services.Log
import com.adobe.marketing.mobile.services.ServiceProvider
import com.adobe.marketing.mobile.services.ui.InAppMessage
import com.adobe.marketing.mobile.services.ui.Presentable
import com.adobe.marketing.mobile.services.ui.PresentationError
import com.adobe.marketing.mobile.services.ui.message.InAppMessageEventListener
import com.adobe.marketing.mobile.services.ui.message.InAppMessageSettings
import com.adobe.marketing.mobile.util.DefaultPresentationUtilityProvider

object InAppMessageCreator {
    private const val LOG_TAG = "InAppMessageCreator"

    private const val sampleHTML = """<!DOCTYPE html>
<html>
  <head>
    <meta
      type="templateProperties"
      name="modal"
      label="adobe-label:modal"
      icon="adobe-icon:modal"
    />
    <meta
      type="templateZone"
      name="default"
      label="Default"
      classname="body"
      definition='["CloseBtn", "Image", "Text", "Buttons"]'
    />
    <meta
      type="templateDefaultAnimations"
      displayanimation="top"
      dismissanimation="top"
    />
    <meta type="templateDefaultSize" width="80" height="60" />
    <meta
      type="templateDefaultPosition"
      verticalalign="center"
      verticalinset="0"
      horizontalalign="center"
      horizontalinset="0"
    />
    <meta type="templateDefaultGesture" />
    <meta type="templateDefaultUiTakeover" enable="true" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <meta charset="UTF-8" />
    <style>
      html,
      body {
        margin: 0;
        padding: 0;
        text-align: center;
        width: 100%;
        height: 100%;
        font-family: adobe-clean, 'Source Sans Pro', -apple-system,
          BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
      }
      h3 {
        margin: 0.4rem auto;
      }
      p {
        margin: 0.4rem auto;
      }
      .body {
        display: flex;
        flex-direction: column;
        background-color: #fff;
        border-radius: 0.3rem;
        color: #333333;
        width: 100vw;
        height: 100vh;
        text-align: center;
        align-items: center;
        background-size: cover;
      }
      .content {
        width: 100%;
        height: 100%;
        display: flex;
        justify-content: center;
        flex-direction: column;
        position: relative;
      }
      a {
        text-decoration: none;
      }
      .image {
        // height: 1rem;
        flex-grow: 4;
        flex-shrink: 1;
        display: flex;
        justify-content: center;
        width: 90%;
        flex-direction: column;
        align-items: center;
      }
      .image img {
        max-height: 100%;
        max-width: 100%;
      }
      .image.empty-image {
        display: none;
      }
      .empty-image ~ .text {
        flex-grow: 1;
      }
      .text {
        text-align: center;
        color: #333333;
        line-height: 1.25rem;
        font-size: 0.875rem;
        padding: 0 0.8rem;
        width: 100%;
        box-sizing: border-box;
      }
      .title {
        line-height: 1.3125rem;
        font-size: 1.025rem;
      }
      .buttons {
        width: 100%;
        display: flex;
        flex-direction: column;
        font-size: 1rem;
        line-height: 1.3rem;
        text-decoration: none;
        text-align: center;
        box-sizing: border-box;
        padding: 0.8rem;
        padding-top: 0.4rem;
        gap: 0.3125rem;
      }
      .button {
        flex-grow: 1;
        background-color: #1473e6;
        color: #ffffff;
        border-radius: 0.25rem;
        cursor: pointer;
        padding: 0.3rem;
        gap: 0.5rem;
      }
      .btnClose {
        color: #000000;
      }
      .closeBtn {
        align-self: flex-end;
        color: #000000;
        width: 1.8rem;
        height: 1.8rem;
        margin-top: 1rem;
        margin-right: 0.3rem;
      }
      .closeBtn img {
        width: 100%;
        height: 100%;
      }
    </style>
    <style type="text/css" id="editor-styles">
      body .body {
        background-color: hsla(0, 44%, 73%, 1);
      }
      .progressBar-title {
        animation: delayedShow 0s 0.3s forwards;
        opacity: 0;
      }
      .progressBar {
        background-color: rgb(225, 225, 225);
        color: rgb(75, 75, 75);
        height: 6px;
        overflow: hidden;
        width: 192px;
        border-radius: 3px;
        z-index: 1;
        margin: 5px;
        animation: delayedShow 0s 0.3s forwards;
        opacity: 0;
      }
      .progressBar-fill {
        height: 6px;
        position: relative;
        width: 136px;
        background-color: #1473e6;
        will-change: transform;
        transition: width 1s;
        transition-timing-function: ease;
        animation: indeterminate-loop-ltr 1s infinite;
        animation-timing-function: ease;
      }
      @keyframes delayedShow {
        to {
          opacity: 1;
        }
      }
      @keyframes indeterminate-loop-ltr {
        from {
          transform: translate(-136px);
        }
        to {
          transform: translate(192px);
        }
      }
      #loadingErrorMsg {
        color: #ff0000;
        font-weight: bold;
      }
    </style>
  </head>
  <body>
    <div class="body">
      <div
        class="closeBtn"
        data-uuid="e8e77030-6fcb-435e-a025-58fd841fe2c7"
        data-btn-style="plain"
      >
        <a
          aria-label="Close"
          class="btnClose"
          href="adbinapp://dismiss?interaction=cancel"
          ><svg
            xmlns="http://www.w3.org/2000/svg"
            height="18"
            viewBox="0 0 18 18"
            width="18"
            class="close"
          >
            <rect
              id="Canvas"
              fill="#ffffff"
              opacity="0"
              width="18"
              height="18"
            ></rect>
            <path
              fill="currentColor"
              xmlns="http://www.w3.org/2000/svg"
              d="M13.2425,3.343,9,7.586,4.7575,3.343a.5.5,0,0,0-.707,0L3.343,4.05a.5.5,0,0,0,0,.707L7.586,9,3.343,13.2425a.5.5,0,0,0,0,.707l.707.7075a.5.5,0,0,0,.707,0L9,10.414l4.2425,4.243a.5.5,0,0,0,.707,0l.7075-.707a.5.5,0,0,0,0-.707L10.414,9l4.243-4.2425a.5.5,0,0,0,0-.707L13.95,3.343a.5.5,0,0,0-.70711-.00039Z"
            ></path></svg
        ></a>
      </div>
      <div class="image" data-uuid="843d1650-c34a-4109-a0cb-7ed246a1fb91">
        <img
          src="https://localhost.corp.adobe.com:8031//InAppBlockImageDefault.92b1cc9c.svg?1745526104629"
          alt=""
        />
      </div>
      <div class="text" data-uuid="6b29bcda-80bd-45e3-a575-b2c6de34af85">
        <h3>Title text123</h3>
        <p>Description text</p>
      </div>
      <div class="buttons" data-uuid="824eaefd-7230-4b3d-b3f0-17d944670158">
        <a
          class="button"
          data-uuid="ae660b64-8bd4-4b97-bc25-0ec109de4971"
          href="adbinapp://dismiss?interaction=clicked"
          >Button</a
        >
      </div>
    </div>
    <script id="injectedScript" type="text/javascript">
      console.log('before window is here');
      if (!window.isInAppBridgeInitialized) {
        console.log('window 2 is here');
        window.isInAppBridgeInitialized = true;
        let shouldRecalculateHeightFromObserver = false;
        function sendMessageToNativeApp(action) {
          try {
            if (window.webkit?.messageHandlers?.inAppContentHeightHandler) {
              window.webkit.messageHandlers.inAppContentHeightHandler.postMessage(
                action
              );
            } else if (window.inAppContentHeightHandler?.run) {
              window.inAppContentHeightHandler.run(action);
            } else {
              console.warn('Native context not available.');
            }
          } catch (err) {
            console.error('Error in sendMessageToNativeApp function:', err);
          }
        }
        // ✅ Wait until all images on page are loaded
        function waitForAllImagesToLoad(callback) {
          const imgs = Array.from(document.querySelectorAll('img'));
          console.log('i am here in wait for all images to load', imgs);
          const pendingImgs = imgs.filter((img) => !img.complete);
          console.log(
            'i am here in pending imgs length is 0',
            pendingImgs.length
          );
          if (pendingImgs.length === 0) {
            callback();
            return;
          }
          let loadedCount = 0;
          pendingImgs.forEach((img) => {
            console.log('i am here in pending imgs');
            img.addEventListener(
              'load',
              () => {
                loadedCount++;
                console.log(
                  'i am here in load event',
                  loadedCount,
                  pendingImgs.length
                );
                if (loadedCount === pendingImgs.length) callback();
              },
              { once: true }
            );
            img.addEventListener(
              'error',
              () => {
                console.log('i am here in error event');
                loadedCount++;
                if (loadedCount === pendingImgs.length) callback();
              },
              { once: true }
            );
          });
        }
        function getContentHeight() {
          return new Promise((resolve) => {
            console.log('get content height is here web naman');
            const body = document.body;
            const html = document.documentElement;
            const maxHeight = 64;
            
            const fullContentHeight = Math.max(
              body.scrollHeight,
              body.offsetHeight,
              html.clientHeight,
              html.scrollHeight,
              html.offsetHeight
            );
            console.log(' body.scrollHeight', body.scrollHeight);
            console.log(' body.offsetHeight', body.offsetHeight);
            console.log(' html.clientHeight', html.clientHeight);
            console.log(' html.scrollHeight', html.scrollHeight);
            console.log(' html.offsetHeight', html.offsetHeight);
            console.log('Calculated full content height:', fullContentHeight);
            const height = Math.min(fullContentHeight, maxHeight);
            if (window.coriolis?.event) {
              console.log(
                '[Bridge] Emitting contentHeightUpdate via Coriolis:',
                height
              );
              window.coriolis.event.emit('contentHeightUpdate', {
                height
              });
            }
            window.parent.postMessage(
              {
                type: 'CONTENT_HEIGHT',
                height
              },
              '*'
            );
            sendMessageToNativeApp(fullContentHeight.toString());
            resolve(fullContentHeight);
          });
        }
        if (!window.sendMessageToNativeApp) {
          window.sendMessageToNativeApp = sendMessageToNativeApp;
        }
        window.getContentHeight = getContentHeight;
        // ✅ Respond to parent postMessage
        window.addEventListener('message', function (event) {
          if (event.data?.type === 'TRIGGER_GET_CONTENT_HEIGHT') {
            getContentHeight();
          }
        });
        // ✅ Recalculate on window load (when images are fully loaded)
        // ✅ Initial height calc AFTER all images have loaded
        window.addEventListener(
          'load',
          () => {
            waitForAllImagesToLoad(() => {
              console.log('i am here in load event');
              getContentHeight();
            });
          },
          { once: true }
        );
        waitForAllImagesToLoad(() => {
          console.log('i am here in load event');
          getContentHeight();
        });
        // ✅ Observe DOM mutations (for dynamic content/layout/image additions)
        const observer = new MutationObserver((mutations) => {
          console.log(
            '[Bridge] DOM mutation detected. Recalculating height...  not calling getContentHeight'
          );
          if (shouldRecalculateHeightFromObserver) {
            console.log(
              '[Bridge] DOM mutation detected. Recalculating height...'
            );
            getContentHeight();
          } else {
            getContentHeight();
            console.log(
              '[Bridge] DOM mutation detected, but skipped recalculating height.'
            );
          }
        });
        observer.observe(document.body, {
          attributes: true,
          childList: true,
          subtree: true,
          characterData: true
        });
        // ✅ Register with Coriolis if available
        if (window.coriolis?.query?.register) {
          console.log(
            '[Bridge] Registering getContentHeight via Coriolis.query'
          );
          window.coriolis.query.register('getContentHeight', () =>
            getContentHeight()
          );
        } else {
          console.warn(
            '[Bridge] Coriolis not ready at injection — using fallback'
          );
          window.getContentHeightForIframe = getContentHeight;
        }
      }
    </script>
  </body>
</html>"""
    private val iamSettings = InAppMessageSettings.Builder()
        .content(sampleHTML)
        .height(40)
        .width(70)
        .maxWidth(300)
        .shouldFitToContent(true)
        .backgroundColor("#FF0000")
        .cornerRadius(10f)
        .displayAnimation(InAppMessageSettings.MessageAnimation.BOTTOM)
        .dismissAnimation(InAppMessageSettings.MessageAnimation.TOP)
/*        .gestureMap(
            mapOf(
                "swipeUp" to "https://adobe.com",
                "swipeDown" to "https://adobe.com",
                "swipeLeft" to "https://adobe.com",
                "swipeRight" to "https://google.com",
                "tapBackground" to "https://google.com"
            )
        )*/

    private val iamEventListener = object : InAppMessageEventListener {
        override fun onBackPressed(message: Presentable<InAppMessage>) {}
        override fun onUrlLoading(message: Presentable<InAppMessage>, url: String): Boolean {
            return false
        }

        override fun onShow(presentable: Presentable<InAppMessage>) {
            val message = presentable.getPresentation()
            message.eventHandler.handleJavascriptMessage("Android") {
                Log.debug("UIServicesView", LOG_TAG, "Message from InAppMessage: $it")
            }
        }

        override fun onHide(presentable: Presentable<InAppMessage>) {}
        override fun onDismiss(presentable: Presentable<InAppMessage>) {}
        override fun onError(presentable: Presentable<InAppMessage>, error: PresentationError) {}
    }

    fun create(): Presentable<InAppMessage> = ServiceProvider.getInstance().uiService.create(
        InAppMessage(iamSettings.build(), iamEventListener),
        DefaultPresentationUtilityProvider()
    )
}
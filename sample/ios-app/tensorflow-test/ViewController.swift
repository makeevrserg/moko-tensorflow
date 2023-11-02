/*
* Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
*/

import UIKit
import MultiPlatformLibrary
import Sketch

class ViewController: UIViewController, SketchViewDelegate {
    
    @IBOutlet weak var sketchView: SketchView!
    @IBOutlet weak var resultLabel: UILabel!
    
    private var objCInterpreter: Interpreter?
    private var tfDigitClassifier: TFDigitClassifier?
    
    private var isInterpreterInited: Bool = false
    private var scope: MainCoroutineScope?
    override func viewDidLoad() {
        super.viewDidLoad()
        scope = MainCoroutineScope()
        sketchView.lineWidth = 30
        sketchView.backgroundColor = UIColor.black
        sketchView.lineColor = UIColor.white
        sketchView.sketchViewDelegate = self
        
        let options = ObjCInterpreterOptions(
            numThreads: 2
        )
        let modelFileRes: ResourcesFileResource = ResHolder().getDigitsClassifierModel()
        
        objCInterpreter = ObjCInterpreter(
            fileResource: modelFileRes,
            options: options
        )
        let interpreter = DigitInterpreter(instance: objCInterpreter!)
        tfDigitClassifier = TFDigitClassifier(interpreter: interpreter, scope: scope!)
        
        tfDigitClassifier?.initialize()
        self.isInterpreterInited = true
    }
    
    
    deinit {
        scope?.close()
        objCInterpreter?.close()
    }

    @IBAction func tapClear(_ sender: Any) {
        sketchView.clear()
        resultLabel.text = "Please draw a digit."
    }
    
    func drawView(_ view: SketchView, didEndDrawUsingTool tool: AnyObject) {
        if isInterpreterInited {
            classifyDrawing()
        }
    }
    
    private func classifyDrawing() {
        guard let tfDigitClassifier = self.tfDigitClassifier else { return }

        UIGraphicsBeginImageContext(sketchView.frame.size)
        sketchView.layer.render(in: UIGraphicsGetCurrentContext()!)
        let drawing = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext();
        
        guard drawing != nil else {
          resultLabel.text = "Invalid drawing."
          return
        }
        
        let rgbData = drawing!.scaledData(with: CGSize(width: Int(tfDigitClassifier.inputImageWidth), height: Int(tfDigitClassifier.inputImageHeight)))
        
        let inputData = NativeInput(nsData: rgbData!)
        tfDigitClassifier.classifyNativeAsync(nativeInput: inputData) { (String) in
            DispatchQueue.main.async {
                self.resultLabel.text = String
            }
        }
    }
    
}

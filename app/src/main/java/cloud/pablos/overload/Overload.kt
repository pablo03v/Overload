package cloud.pablos.overload

import android.app.Application
import android.content.Context
import android.widget.Toast
import org.acra.config.mailSender
import org.acra.config.toast
import org.acra.data.StringFormat
import org.acra.ktx.initAcra

class Overload : Application() {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)

        initAcra {
            buildConfigClass = BuildConfig::class.java
            reportFormat = StringFormat.JSON

            toast {
                text = getString(R.string.acra_toastText)
                length = Toast.LENGTH_LONG
            }

            mailSender {
                mailTo = getString(R.string.acra_mailTo)
                reportAsFile = true
                // defaults to ACRA-report.stacktrace
                reportFileName = "crash.json"
                subject = getString(R.string.acra_mailSubject)
                // defaults to empty
                body = getString(R.string.acra_mailBody)
            }
        }
    }
}

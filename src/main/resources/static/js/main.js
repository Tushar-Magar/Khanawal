document.addEventListener('DOMContentLoaded', function () {
    // Auto-dismiss alert boxes after 5 seconds
    const alerts = document.querySelectorAll('.alert-dismissible');
    alerts.forEach(function (alert) {
        setTimeout(function () {
            try {
                const bsAlert = bootstrap.Alert.getOrCreateInstance(alert);
                if (bsAlert) bsAlert.close();
            } catch (e) {
                alert.style.display = 'none';
            }
        }, 5000);
    });

    // Quantity selector buttons
    document.querySelectorAll('.qty-btn-minus').forEach(function (btn) {
        btn.addEventListener('click', function () {
            const input = this.parentElement.querySelector('.qty-input');
            let val = parseInt(input.value) || 1;
            if (val > 1) {
                input.value = val - 1;
                const form = this.closest('form');
                if (form && form.classList.contains('auto-submit-form')) {
                    form.submit();
                }
            }
        });
    });

    document.querySelectorAll('.qty-btn-plus').forEach(function (btn) {
        btn.addEventListener('click', function () {
            const input = this.parentElement.querySelector('.qty-input');
            let val = parseInt(input.value) || 1;
            if (val < 99) {
                input.value = val + 1;
                const form = this.closest('form');
                if (form && form.classList.contains('auto-submit-form')) {
                    form.submit();
                }
            }
        });
    });
});
